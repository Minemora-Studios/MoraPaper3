package net.minemora.morapaper;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MoraGlobalRegionScheduler implements GlobalRegionScheduler {

    private static MoraGlobalRegionScheduler instance;

    private final Set<MoraScheduledTask> tasks = ConcurrentHashMap.newKeySet();

    public static MoraGlobalRegionScheduler getInstance() {
        if (instance == null) {
            instance = new MoraGlobalRegionScheduler();
        }
        return instance;
    }

    @Override
    public void execute(@NotNull Plugin plugin, @NotNull Runnable run) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin " + plugin.getName() + " is not enabled");
        }
        Bukkit.getScheduler().runTask(plugin, run);
    }

    @Override
    public @NotNull ScheduledTask run(@NotNull Plugin plugin, @NotNull Consumer<ScheduledTask> task) {
        return runDelayed(plugin, task, 1);
    }

    @Override
    public @NotNull ScheduledTask runDelayed(@NotNull Plugin plugin, @NotNull Consumer<ScheduledTask> task, long delayTicks) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin " + plugin.getName() + " is not enabled");
        }
        MoraScheduledTask scheduledTask = new MoraScheduledTask(plugin, task, false);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, scheduledTask::execute, delayTicks);
        scheduledTask.setHandle(bukkitTask);
        tasks.add(scheduledTask);
        return scheduledTask;
    }

    @Override
    public @NotNull ScheduledTask runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<ScheduledTask> task, long initialDelayTicks, long periodTicks) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin " + plugin.getName() + " is not enabled");
        }
        MoraScheduledTask scheduledTask = new MoraScheduledTask(plugin, task, true);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, scheduledTask::execute, initialDelayTicks, periodTicks);
        scheduledTask.setHandle(bukkitTask);
        tasks.add(scheduledTask);
        return scheduledTask;
    }

    @Override
    public void cancelTasks(@NotNull Plugin plugin) {
        tasks.removeIf(task -> {
            if (task.getOwningPlugin().equals(plugin)) {
                task.cancel();
                return true;
            }
            return false;
        });
    }

    private class MoraScheduledTask implements ScheduledTask {

        private final Plugin plugin;
        private final Consumer<ScheduledTask> task;
        private final boolean repeating;
        private BukkitTask handle;
        private final AtomicReference<ExecutionState> state = new AtomicReference<>(ExecutionState.IDLE);

        public MoraScheduledTask(Plugin plugin, Consumer<ScheduledTask> task, boolean repeating) {
            this.plugin = plugin;
            this.task = task;
            this.repeating = repeating;
        }

        public void setHandle(BukkitTask handle) {
            this.handle = handle;
        }

        public void execute() {
            if (isCancelled()) return;

            state.set(ExecutionState.RUNNING);
            try {
                task.accept(this);
            } catch (Throwable t) {
                plugin.getLogger().warning("Exception while executing MoraScheduledTask: " + t.getMessage());
            } finally {
                if (!repeating) {
                    state.set(ExecutionState.FINISHED);
                    tasks.remove(this);
                } else if (state.get() == ExecutionState.RUNNING) {
                    state.set(ExecutionState.IDLE);
                } else {
                    state.set(ExecutionState.CANCELLED_RUNNING);
                    tasks.remove(this);
                }
            }
        }

        @Override
        public @NotNull Plugin getOwningPlugin() {
            return plugin;
        }

        @Override
        public boolean isRepeatingTask() {
            return repeating;
        }

        @Override
        public @NotNull CancelledState cancel() {
            ExecutionState current = state.get();
            switch (current) {
                case IDLE:
                    state.set(ExecutionState.CANCELLED);
                    if (handle != null && !handle.isCancelled()) {
                        handle.cancel();
                    }
                    tasks.remove(this);
                    return CancelledState.CANCELLED_BY_CALLER;
                case RUNNING:
                    if (!repeating) return CancelledState.RUNNING;
                    state.set(ExecutionState.CANCELLED_RUNNING);
                    return CancelledState.NEXT_RUNS_CANCELLED;
                case CANCELLED_RUNNING:
                    return CancelledState.NEXT_RUNS_CANCELLED_ALREADY;
                case FINISHED:
                    return CancelledState.ALREADY_EXECUTED;
                default:
                    return CancelledState.CANCELLED_ALREADY;
            }
        }

        @Override
        public @NotNull ExecutionState getExecutionState() {
            return state.get();
        }
    }
}
