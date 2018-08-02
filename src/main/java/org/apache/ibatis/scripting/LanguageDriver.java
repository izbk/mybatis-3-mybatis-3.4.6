/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.scripting;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

/**
 * 语言驱动器接口
 * MyBatis 从 3.2 开始支持可插拔脚本语言，这允许你插入一种脚本语言驱动，并基于这种语言来编写动态 SQL 查询语句
 * 一旦设定了自定义语言驱动，你就可以在 mybatis-config.xml 文件中将它设置为默认语言：
 * <typeAliases>
 *   <typeAlias type="org.sample.MyLanguageDriver" alias="myLanguage"/>
 * </typeAliases>
 * <settings>
 *   <setting name="defaultScriptingLanguage" value="myLanguage"/>
 * </settings>
 * 除了设置默认语言，你也可以针对特殊的语句指定特定语言，可以通过如下的 lang 属性来完成：
 * <select id="selectBlog" lang="myLanguage">
 *   SELECT * FROM BLOG
 * </select>
 * 或者，如果你使用的是映射器接口类，在抽象方法上加上 @Lang 注解即可：
 * public interface Mapper {
 *   @Lang(MyLanguageDriver.class)
 *   @Select("SELECT * FROM BLOG")
 *   List<Blog> selectBlog();
 * }
 */
public interface LanguageDriver {

  /**
   * Creates a {@link ParameterHandler} that passes the actual parameters to the the JDBC statement.
   * 
   * @param mappedStatement The mapped statement that is being executed
   * @param parameterObject The input parameter object (can be null) 
   * @param boundSql The resulting SQL once the dynamic language has been executed.
   * @return
   * @author Frank D. Martinez [mnesarco]
   * @see DefaultParameterHandler
   */
  ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

  /**
   * Creates an {@link SqlSource} that will hold the statement read from a mapper xml file. 
   * It is called during startup, when the mapped statement is read from a class or an xml file.
   * 
   * @param configuration The MyBatis configuration
   * @param script XNode parsed from a XML file
   * @param parameterType input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be null.
   * @return
   */
  SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);

  /**
   * Creates an {@link SqlSource} that will hold the statement read from an annotation.
   * It is called during startup, when the mapped statement is read from a class or an xml file.
   * 
   * @param configuration The MyBatis configuration
   * @param script The content of the annotation
   * @param parameterType input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be null.
   * @return 
   */
  SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);

}
