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
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * 插件接口，使用插件必须实现此接口
 * @author Clinton Begin
 */
public interface Interceptor {

  // 它将直接覆盖你所拦截对象原有的方法，因此它是插件的核心方法。
  // Intercept里面有个参数Invocation对象，通过它可以反射调度原来对象的方法
  Object intercept(Invocation invocation) throws Throwable;

  // 作用是给被拦截对象生成一个代理对象，并返回它。target是被拦截对象
  Object plugin(Object target);

  // 允许在plugin元素中配置所需参数，方法在插件初始化的时候就被调用了一次
  void setProperties(Properties properties);

}
