/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.scripting.xmltags;

import java.util.HashMap;
import java.util.Map;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * 为POJO对象Map化提供了很好的借鉴,抹平了访问POJO和Map对象的差异.
 * 为各个动态sql节点实现类(诸如ChooseSqlNode等)提供进行判断的上下信息.确保判断等操作的完整实现.
 * @author Clinton Begin
 */
public class DynamicContext {

  public static final String PARAMETER_OBJECT_KEY = "_parameter";
  public static final String DATABASE_ID_KEY = "_databaseId";

  static {
    // Mybatis中采用了Ognl来计算动态sql语句，DynamicContext类中的这个静态初始块，很好的说明了这一点
    OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
  }

  private final ContextMap bindings;
  private final StringBuilder sqlBuilder = new StringBuilder();
  private int uniqueNumber = 0;

  public DynamicContext(Configuration configuration, Object parameterObject) {
    // 构造函数, 对传入的parameterObject对象进行“map”化处理;
    // 传入的pojo对象，会被当作一个键值对数据来源来进行处理，读取这个pojo对象的接口,依然是Map对象(依然是以Map接口方式来进行读取)。
    /*
     * 在DynamicContext的构造函数中，可以看到:
     *    1. 根据传入的参数对象是否为Map类型，有两个不同构造ContextMap的方式。
     *    2. 而ContextMap作为一个继承了HashMap的对象，作用就是用于统一参数的访问方式：用Map接口方法来访问数据。
     *    具体来说:
     *         2.1 当传入的参数对象不是Map类型时，Mybatis会将传入的POJO对象用MetaObject对象来封装，
     *         2.2 当动态计算sql过程需要获取数据时，用Map接口的get方法包装 MetaObject对象的取值过程。
     *         2.3 ContextMap覆写的get方法正是为了上述目的.具体参见下面的`ContextMap`覆写的get方法里的详细解释.
     *    3. 这里结合着DefaultSqlSession类中的私有方法wrapCollection一起看效果更佳. wrapCollection方法保证了即使用户传入集合类型时,
     *    在构造DynamicContext时使用parameterObject参数依然是个Map类型.
     */
    if (parameterObject != null && !(parameterObject instanceof Map)) {
      // 当用户传入的参数是普通的POJO
      MetaObject metaObject = configuration.newMetaObject(parameterObject);
      bindings = new ContextMap(metaObject);
    } else {
      // 当用户传入的参数null或Map类型时
      bindings = new ContextMap(null);
    }
    // 设置_parameter和_databaseId
    bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
    bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
  }

  public Map<String, Object> getBindings() {
    return bindings;
  }

  public void bind(String name, Object value) {
    bindings.put(name, value);
  }

  public void appendSql(String sql) {
    sqlBuilder.append(sql);
    sqlBuilder.append(" ");
  }

  public String getSql() {
    return sqlBuilder.toString().trim();
  }

  public int getUniqueNumber() {
    return uniqueNumber++;
  }

  /**
   * ContextMap作为一个继承了HashMap的对象，作用就是用于统一参数的访问方式：用Map接口方法来访问数据。
   */
  static class ContextMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 2977601501966151582L;

    private MetaObject parameterMetaObject;
    public ContextMap(MetaObject parameterMetaObject) {
      this.parameterMetaObject = parameterMetaObject;
    }

    @Override
    public Object get(Object key) {
      String strKey = (String) key;
      //数据来源是
      //  1. 下面的super.put(key,object).
      //  2. 客户端主动向ContextMap插入的键值对(例如DynamicContext构造函数中就插入了两个内置的:_parameter和_databaseId),
      // 或者在bind标签中添加的(XMLScriptBuilder.BindHandler类)
      if (super.containsKey(strKey)) {
        return super.get(strKey);
      }
      // 下面这部分就是将POJO的值以Map接口的get方式暴露;
      // 细节就是:
      //    1. 下面这部分一旦从POJO中取到了对应key的值,则推入到将其进行封装的ContextMap实例中(即super.put(strKey, object);因为其直接继承自HashMap).
      //    2. 而上面这部分的super.get(key)正是和这个super.put(strKey, object)对应的

      if (parameterMetaObject != null) {
        // issue #61 do not modify the context when reading
        return parameterMetaObject.getValue(strKey);
      }

      return null;
    }
  }

  static class ContextAccessor implements PropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object name)
        throws OgnlException {
      Map map = (Map) target;// target为ContextMap,所以可以安全地转换为Map

      Object result = map.get(name);
      if (map.containsKey(name) || result != null) {
        return result;
      }

      // 构造DynamicContext实例时,插入到ContextMap实例中的键值对:{ "_parameter" : parameterObject }
      Object parameterObject = map.get(PARAMETER_OBJECT_KEY);
      if (parameterObject instanceof Map) {
        return ((Map)parameterObject).get(name);
      }

      return null;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value)
        throws OgnlException {
      Map<Object, Object> map = (Map<Object, Object>) target;
      map.put(name, value);
    }

    @Override
    public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
      return null;
    }

    @Override
    public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
      return null;
    }
  }
}