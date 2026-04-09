package com.matheushrs.psp_void_tools_orch.registry;

import com.matheushrs.psp_void_tools_orch.tools.Tool;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * In-memory registry of all tools loaded from psp-void-orch OpenAPI spec.
 * Tools are registered once on application startup via {@link com.matheushrs.psp_void_tools_orch.config.ToolsConfig}.
 */
@Component
public class ToolRegistry {

    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(Tool tool) {
        tools.put(tool.getName(), tool);
    }

    public void registerAll(List<Tool> toolList) {
        toolList.forEach(this::register);
    }

    public Optional<Tool> findByName(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public List<Tool> findAll() {
        return List.copyOf(tools.values());
    }

    public boolean isEmpty() {
        return tools.isEmpty();
    }
}
