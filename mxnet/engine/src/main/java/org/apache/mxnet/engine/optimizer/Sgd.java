/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.mxnet.engine.optimizer;

import org.apache.mxnet.engine.lrscheduler.MxLearningRateTracker;
import software.amazon.ai.ndarray.NDArray;
import software.amazon.ai.ndarray.NDList;

public class Sgd extends MxOptimizer {

    private float momentum;
    private boolean lazyUpdate;

    public Sgd(
            float rescaleGrad,
            float weightDecays,
            float clipGrad,
            MxLearningRateTracker lrScheduler,
            int beginNumUpdate,
            float momentum,
            boolean lazyUpdate) {
        super(rescaleGrad, weightDecays, clipGrad, lrScheduler, beginNumUpdate);
        this.momentum = momentum;
        this.lazyUpdate = lazyUpdate;
    }

    @Override
    public NDList createState(int index, NDArray weight) {
        // TODO: Support Mixed precision and Sparse
        if (momentum != 0.0) {
            return new NDList(weight.zerosLike());
        }
        return null;
    }

    @Override
    public void update(int index, NDArray weight, NDArray grad, NDList state) {
        // TODO: Support Mixed precision Sparse
        if (state != null) {
            weight.getNDArrayInternal()
                    .sgdMomUpdate(
                            grad,
                            state.head(),
                            getLearningRate(),
                            weightDecays,
                            momentum,
                            rescaleGrad,
                            clipGrad,
                            lazyUpdate);
        } else {
            weight.getNDArrayInternal()
                    .sgdUpdate(
                            grad,
                            getLearningRate(),
                            weightDecays,
                            rescaleGrad,
                            clipGrad,
                            lazyUpdate);
        }
    }
}