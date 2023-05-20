export class Machine{
    title : string;
    log_dir: string;
    tensorboard_url : string;
    docker_url: string

    constructor(title: string, log_dir: string, tensorboard_url: string, docker_url: string) {
        this.title = title;
        this.log_dir = log_dir;
        this.tensorboard_url = tensorboard_url;
        this.docker_url = docker_url;
    }
}

