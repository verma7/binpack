package com.uber.aurora.resallocsim;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    enum BinPackingAlgorithm { FIRST_FIT, BEST_FIT, WORST_FIT, EPVM};

    // Constants for the simulation
    final int kMachines = 10;
    final int kTasks = 30;
    final BinPackingAlgorithm bpAlgorithm = BinPackingAlgorithm.FIRST_FIT;

    List<Machine> machines;
    List<Resource> tasks;

    public class Resource {
        String name;
        int cpu_cores;
        int mem_mb;
        int disk_mb;

        public Resource(String name, int cpu_cores, int mem_mb, int disk_mb) {
            this.name = name;
            this.cpu_cores = cpu_cores;
            this.mem_mb = mem_mb;
            this.disk_mb = disk_mb;
        }

        public String toString() {
            return name + "(" + cpu_cores + "," + mem_mb + "," + disk_mb + ")";
        }
    }

    public class Machine {
        Resource used;
        Resource total;

        public String toString() {
            return "{" + used + " : " + total + "}";
        }
    }

    public void setup() {
        machines = new ArrayList<>();
        for (int i = 0; i < kMachines; i++) {
            Machine m = new Machine();
            m.total = new Resource("machine_total_" + i, 24, 128, 1024);
            m.used = new Resource("machine_used_" + i, 0, 0, 0);
            machines.add(m);
        }

        tasks = new ArrayList<>();
        for (int i = 0; i < kTasks; i++) {
            tasks.add(new Resource("task_" + i, 12, 64, 512));
        }

    }

    int findMachineForTask(int taskId) {
        Resource task = tasks.get(taskId);
        switch(bpAlgorithm) {
            case FIRST_FIT: {
                for (int i = 0; i < machines.size(); i++) {
                    Machine m = machines.get(i);
                    if ((task.cpu_cores <= m.total.cpu_cores - m.used.cpu_cores) &&
                            (task.mem_mb <= m.total.mem_mb - m.used.mem_mb) &&
                            (task.disk_mb <= m.total.disk_mb - m.used.disk_mb)) {
                        return i;
                    }
                }
                break;
            }
            case BEST_FIT: {
                for (int i = 0; i < machines.size(); i++) {
                    Machine m = machines.get(i);
                }
                break;
            }
            default: {
                System.err.println("Unknown bin packing algorithm: " + bpAlgorithm);
                System.exit(1);
            }
        }
        return -1;
    }

    void scheduleTaskOnMachine(int taskId, int machineId) {
        Resource task = tasks.get(taskId);
        if (machineId == -1) {
            System.err.println("Could not find machine for task: " + task);
            return;
        }
        Machine machine = machines.get(machineId);

        System.out.println("Scheduling " + task + " on " + machine);

        machine.used.cpu_cores += task.cpu_cores;
        machine.used.mem_mb += task.mem_mb;
        machine.used.disk_mb += task.disk_mb;
    }

    public void schedule() {
        for (int taskId = 0; taskId < kTasks; taskId++) {
            int machineId = findMachineForTask(taskId);
            scheduleTaskOnMachine(taskId, machineId);
        }
    }

    public void simulate() {
        setup();
        schedule();
    }

    public static void main(String args[]) {
        Simulator sim = new Simulator();
        sim.simulate();
    }
}
