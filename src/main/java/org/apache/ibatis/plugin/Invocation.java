/**
 *    Copyright 2009-2017 the original author or authors.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 被拦截方法的信息封装
 * @author Clinton Begin
 */
public class Invocation {

  private final Object target;
  private final Method method;
  private final Object[] args;

  public Invocation(Object target, Method method, Object[] args) {
    this.target = target;
    this.method = method;
    this.args = args;
  }

  public Object getTarget() {
    return target;
  }

  public Method getMethod() {
    return method;
  }

  public Object[] getArgs() {
    return args;
  }

  // 这个方法会调度被代理对象的真实方法， 所以我们通过这个方法直接调用被代理对象原来的方法
  // 如果多个插件的话，我们知道会生成多层代理对象，那么每层被代理都可以通过Invocation调用这个proceed方法，
  // 所以在多个插件的环境下，调度proceed()方法时，MyBatis总是从最后一个代理对象运行到第一个代理对象，
  // 最后是真实被拦截的对象方法被运行
  public Object proceed() throws InvocationTargetException, IllegalAccessException {
    return method.invoke(target, args);
  }

}
