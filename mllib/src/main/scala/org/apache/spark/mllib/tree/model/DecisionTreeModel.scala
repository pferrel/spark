/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.mllib.tree.model

import org.apache.spark.annotation.Experimental
import org.apache.spark.mllib.tree.configuration.Algo._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.Vector

/**
 * :: Experimental ::
 * Decision tree model for classification or regression.
 * This model stores the decision tree structure and parameters.
 * @param topNode root node
 * @param algo algorithm type -- classification or regression
 */
@Experimental
class DecisionTreeModel(val topNode: Node, val algo: Algo) extends Serializable {

  /**
   * Predict values for a single data point using the model trained.
   *
   * @param features array representing a single data point
   * @return Double prediction from the trained model
   */
  def predict(features: Vector): Double = {
    topNode.predictIfLeaf(features)
  }

  /**
   * Predict values for the given data set using the model trained.
   *
   * @param features RDD representing data points to be predicted
   * @return RDD[Int] where each entry contains the corresponding prediction
   */
  def predict(features: RDD[Vector]): RDD[Double] = {
    features.map(x => predict(x))
  }

  /**
   * Get number of nodes in tree, including leaf nodes.
   */
  def numNodes: Int = {
    1 + topNode.numDescendants
  }

  /**
   * Get depth of tree.
   * E.g.: Depth 0 means 1 leaf node.  Depth 1 means 1 internal node and 2 leaf nodes.
   */
  def depth: Int = {
    topNode.subtreeDepth
  }

  /**
   * Print full model.
   */
  override def toString: String = algo match {
    case Classification =>
      s"DecisionTreeModel classifier\n" + topNode.subtreeToString(2)
    case Regression =>
      s"DecisionTreeModel regressor\n" + topNode.subtreeToString(2)
    case _ => throw new IllegalArgumentException(
      s"DecisionTreeModel given unknown algo parameter: $algo.")
  }

}
