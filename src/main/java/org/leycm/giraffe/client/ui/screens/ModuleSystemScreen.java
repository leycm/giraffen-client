package org.leycm.giraffe.client.ui.screens;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffe.client.module.Modules;
import org.leycm.giraffe.client.module.common.BaseModule;
import org.leycm.giraffe.client.ui.widgets.ModuleCategoryList;
import org.leycm.giraffe.client.ui.widgets.ModuleEditPanel;
import org.leycm.giraffe.client.ui.widgets.ModuleEditPanelManager;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleSystemScreen extends ModernScreen {
    private static final int SIDEBAR_WIDTH = 200;
    private static final int SEARCH_HEIGHT = 25;
    private static final int PADDING = 20;

    private TextFieldWidget searchField;
    private ModuleCategoryList categoryList;
    private ModuleEditPanelManager editPanelManager;

    private String searchQuery = "";
    private final Map<String, List<BaseModule>> modulesByCategory = new HashMap<>();
    private final Map<String, Boolean> categoryExpanded = new HashMap<>();

    public ModuleSystemScreen() {
        super("module-screen", "Module System");
        groupModulesByCategory();
    }

    @Override
    protected void onInit() {
        initializeSearchField();
        initializeCategoryList();
        initializeEditPanelManager();
    }

    @Override
    protected void onRender() {
        renderSearchField();
        renderCategoryList();
        renderEditPanelManager();
    }

    private void initializeSearchField() {
        this.searchField = new TextFieldWidget(
                this.textRenderer,
                PADDING,
                PADDING + 5,
                SIDEBAR_WIDTH - PADDING * 2,
                SEARCH_HEIGHT,
                Text.literal("Search modules...")
        );
        this.searchField.setPlaceholder(Text.literal("Search modules..."));
        this.searchField.setChangedListener(this::onSearchChanged);
        this.addDrawableChild(searchField);
    }

    private void initializeCategoryList() {
        int listY = PADDING + SEARCH_HEIGHT + 15;
        this.categoryList = new ModuleCategoryList(
                PADDING,
                listY,
                SIDEBAR_WIDTH - PADDING * 2,
                this.height - listY - PADDING,
                this::onModuleSelected,
                this::onModuleToggled,
                this::onCategoryToggled
        );

        updateCategoryList();
        this.addDrawableChild(categoryList);
    }

    private void initializeEditPanelManager() {
        int panelX = SIDEBAR_WIDTH + 25 + PADDING;
        int panelWidth = this.width - SIDEBAR_WIDTH - PADDING * 2;
        int panelY = PADDING + 25;
        int panelHeight = this.height - PADDING * 2;

        this.editPanelManager = new ModuleEditPanelManager(
                panelX,
                panelY,
                panelWidth,
                panelHeight
        );

        this.addDrawableChild(editPanelManager);
    }

    private void groupModulesByCategory() {
        modulesByCategory.clear();
        for (BaseModule module : Modules.getModules().values()) {
            String category = module.getCategory();
            modulesByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(module);
            categoryExpanded.putIfAbsent(category, true);
        }

        for (List<BaseModule> modules : modulesByCategory.values()) {
            modules.sort(Comparator.comparing(BaseModule::getDisplayName));
        }
    }

    private void updateCategoryList() {
        if (categoryList == null) return;

        categoryList.clear();

        Map<String, List<BaseModule>> filteredCategories = getFilteredModules();

        for (Map.Entry<String, List<BaseModule>> entry : filteredCategories.entrySet()) {
            String category = entry.getKey();
            List<BaseModule> modules = entry.getValue();

            boolean expanded = categoryExpanded.getOrDefault(category, true);
            categoryList.addCategory(category, modules, expanded);
        }
    }

    private Map<String, List<BaseModule>> getFilteredModules() {
        if (searchQuery.isEmpty()) {
            return modulesByCategory;
        }

        Map<String, List<BaseModule>> filtered = new HashMap<>();
        String query = searchQuery.toLowerCase();

        for (Map.Entry<String, List<BaseModule>> entry : modulesByCategory.entrySet()) {
            List<BaseModule> matchingModules = entry.getValue().stream()
                    .filter(module -> module.getDisplayName().toLowerCase().contains(query) ||
                            module.getId().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            if (!matchingModules.isEmpty()) {
                filtered.put(entry.getKey(), matchingModules);
            }
        }

        return filtered;
    }

    private void onSearchChanged(String query) {
        this.searchQuery = query;
        updateCategoryList();
    }

    private void onModuleSelected(BaseModule module) {
        editPanelManager.addOrSelectModule(module);
    }

    private void onModuleToggled(@NotNull BaseModule module) {
        module.toggle();
        categoryList.refresh();
    }

    private void onCategoryToggled(String category) {
        boolean currentState = categoryExpanded.getOrDefault(category, true);
        categoryExpanded.put(category, !currentState);
        updateCategoryList();
    }

    private void renderSearchField() {
    }

    private void renderCategoryList() {
    }

    private void renderEditPanelManager() {
    }
}