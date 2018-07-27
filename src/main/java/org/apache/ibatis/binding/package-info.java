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
/**
 * Bings mapper interfaces with mapped statements
 * Mapper的动态代理
 * 采用Mapper动态代理方法只需要编写相应的Mapper接口，那么Mybatis框架根据接口定义创建接口的动态代理对象，代理对象的方法体同Dao接口实现类方法。
 *  Mapper接口开发需要遵循以下规范：
 * 1、Mapper.xml文件中的namespace与mapper接口的全类名相同。
 * 2、Mapper接口方法名和Mapper.xml中定义的每个statement的id相同。
 * 3、Mapper接口方法的输入参数类型和mapper.xml中定义的每个sql 的parameterType的类型相同。
 * 4、Mapper接口方法的输出参数类型和mapper.xml中定义的每个sql的resultType的类型相同。
 */
package org.apache.ibatis.binding;
