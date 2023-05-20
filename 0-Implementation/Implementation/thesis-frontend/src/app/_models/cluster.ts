import { Machine } from './machine';

export class Cluster {
    title:string;
    machines: Machine[];

    constructor(title:string, machines: Machine[]) {
        this.title = title;
        this.machines = machines;
    }

}