import user

import argparse
import os, json
import tensorflow as tf
import logging

logger = tf.get_logger()
logging.basicConfig(filename='logs/app.log', filemode='w', format='%(name)s - %(levelname)s - %(message)s')
logger.setLevel(logging.DEBUG)
tf.autograph.set_verbosity(10, alsologtostdout=True)

#Logging Console
def _parse_args():
    parser = argparse.ArgumentParser(
        description='Train mnist model using ParameterServerStrategy,  reads the tensorflow configuration from the given json file')

    parser.add_argument('-f','--file',dest='json', help="Directory for json file",
    required=True)

    parser.add_argument('-p','--modelpath',dest='mpath', help="Directory where to save model and checkpoints",
    required=True)

    parser.add_argument('-m','--model',dest='model', help="Model function name initiate training on",
    required=True)

    args = parser.parse_args()
    return args

def set_tf_config(json_path):
    with open(json_path, 'r') as f:
        tf_config = json.load(f)
    os.environ['TF_CONFIG'] = json.dumps(tf_config)

args = _parse_args()
set_tf_config(args.json)

model_path = args.mpath
strategy = tf.distribute.experimental.ParameterServerStrategy()

method_to_run = getattr(user, args.model)

#Estimator Configuration
config = tf.estimator.RunConfig(
    train_distribute=strategy,
    save_summary_steps=user.SUMMARY_STEPS,
    save_checkpoints_steps=user.CHECK_POINTS,
)

estimator = None
if(user.keras_to_estimator):
    estimator = tf.keras.estimator.model_to_estimator(
        config=config,
        keras_model=method_to_run,
        model_dir=model_path
    )
else:
    estimator = tf.estimator.Estimator(
        config=config,
        model_fn=method_to_run,
        model_dir=model_path
    )

#Begin training and then with the use of the evaluator server, will evaluate the model with the given path in the configuration
tf.estimator.train_and_evaluate(
    estimator,
    train_spec=tf.estimator.TrainSpec(input_fn=user.input_fn, max_steps=user.steps[args.model]),
    eval_spec=tf.estimator.EvalSpec(input_fn=user.eval_fn)
)

