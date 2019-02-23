package com.exloki.forux.ecorewards.managers;

import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskManager {

    private Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    public void registerTask(UUID owner, BukkitTask task) {
        activeTasks.put(owner, task);
    }

    public void unregisterTask(UUID owner) {
        activeTasks.remove(owner);
    }

    public void flushTasks() {
        for(BukkitTask task : activeTasks.values()) {
            if(task != null) {
                task.cancel();
            }
        }

        activeTasks.clear();
    }
}
