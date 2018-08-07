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

/**
 *
 * SqlNode接口主要用来处理CRUD节点下的各类动态标签，对每个动态标签，mybatis都提供了对应的SqlNode实现，
 * 这些动态标签可以相互嵌套且实现上采用单向链表进行应用，这样后面如果需要增加其他动态标签，就只需要新增
 * 对应的SqlNode实现就能支持，其中MixedSqlNode代表了所有具体SqlNode的集合，其他分别代表了一种类型的SqlNode
 * @author Clinton Begin
 */
public interface SqlNode {
  // DynamicContext的作用就是为各个动态sql节点实现类(诸如ChooseSqlNode等)提供进行判断的上下信息.确保判断等操作的完整实现
  boolean apply(DynamicContext context);
}
