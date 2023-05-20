#Define Imports
import tensorflow as tf
import numpy as np
import tensorflow_datasets as tfds

#Define Variables
BUFFER_SIZE = 10000
BATCH_SIZE = 64
LEARNING_RATE = 1e-4
CHECK_POINTS = 1000
SUMMARY_STEPS = 100

#User has to name these the same as the method functions
steps = dict({
    'model1': 10000,
    'model2': 15000
})

keras_to_estimator=True

#Input, will be used to insert into the estimator. This function is what dataset the model will train with.
def input_fn():
    datasets, info = tfds.load(name='mnist',
                                with_info=True,
                                as_supervised=True)
  
    mnist_dataset = datasets['train']
    
    def scale(image, label):
        image = tf.cast(image, tf.float32)
        image /= 255
        return image, label

    return mnist_dataset.map(scale).shuffle(BUFFER_SIZE).repeat().batch(BATCH_SIZE).prefetch(buffer_size=tf.data.experimental.AUTOTUNE)

#Evaluation, will be used to insert into the estimator. This function is what will evaluate the result from the model that it will train with.
def eval_fn():
  datasets, info = tfds.load(name='mnist',
                                with_info=True,
                                as_supervised=True)
  mnist_dataset = datasets['test']

  def scale(image, label):
        image = tf.cast(image, tf.float32)
        image /= 255
        return image, label

  return mnist_dataset.map(scale).repeat().batch(BATCH_SIZE).prefetch(buffer_size=tf.data.experimental.AUTOTUNE)

#Models : Neural Network models that the estimator will use.
def model1():
    model = tf.keras.Sequential([
        tf.keras.layers.Conv2D(32, 3, activation='relu', input_shape=(28, 28, 1)),
        tf.keras.layers.MaxPooling2D(),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(64, activation='relu'),
        tf.keras.layers.Dense(10)
    ])

    model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
        optimizer=tf.keras.optimizers.Adam(),
        metrics=['accuracy'])

    return model

def model2():
    model = tf.keras.Sequential([
        tf.keras.layers.Conv2D(32, 3, activation='relu', input_shape=(28, 28, 1)),
        tf.keras.layers.MaxPooling2D(),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(64, activation='relu'),
        tf.keras.layers.Dense(10)
    ])

    model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
        optimizer=tf.keras.optimizers.Adam(),
        metrics=['accuracy'])

    return model


