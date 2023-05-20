#Define Imports
import tensorflow as tf
import numpy as np
import tensorflow_datasets as tfds

#Define Variables
BUFFER_SIZE = 10000
BATCH_SIZE = 64
LEARNING_RATE = 1e-4
#Define Checkpoints
CHECK_POINTS = 1000
SUMMARY_STEPS = 100

#Set true if the keras model has been compiled but not migrated to use for estimator.
keras_to_estimator=False

# User has to name these the same as the method functions
steps = dict({
    'model1': 10000,
    'model2': 20000
})

def input_fn():
    #Input, will be used to insert into the estimator. This function is what dataset the model will train with.

def eval_fn():
    #Evaluation, will be used to insert into the estimator. This function is what will evaluate the result
    #from the model that it will train with.

def model1(features, labels, mode):
    #Models : Neural Network models that the estimator will use.

def model2(features, labels, mode):
    #Models : Neural Network models that the estimator will use.

def model3(features, labels, mode):
    #Models : Neural Network models that the estimator will use.

def model4(features, labels, mode):
    #Models : Neural Network models that the estimator will use.

