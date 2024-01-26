package com.cool.request.view.tool;

/**
 * 左侧视图切换器，由 {@link MainToolWindows} 实现。
 *
 * @example <pre>
 * {@code ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project,(t)->{})
 * }
 * </pre>
 */

public interface ToolActionPageSwitcher extends Provider {
    public void goToByName(String name, Object attachData);
}
