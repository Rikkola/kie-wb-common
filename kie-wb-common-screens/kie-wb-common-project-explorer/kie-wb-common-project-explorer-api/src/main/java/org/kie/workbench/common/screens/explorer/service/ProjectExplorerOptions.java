package org.kie.workbench.common.screens.explorer.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProjectExplorerOptions {

    private final HashSet<Option> set;

    public ProjectExplorerOptions() {
        this.set = new HashSet<Option>();
    }

    // only used by tests
    @Deprecated
    public ProjectExplorerOptions( Option... options ) {
        this.set = new HashSet<Option>( Arrays.asList( options ) );
    }

    public boolean areHiddenFilesVisible() {
        return set.contains( Option.INCLUDE_HIDDEN_ITEMS );
    }

    public boolean isHeaderNavigationHidden() {
        return set.contains( Option.NO_CONTEXT_NAVIGATION );
    }

    public boolean isBreadCrumbNavigationVisible() {
        return set.contains( Option.BREADCRUMB_NAVIGATOR );
    }

    public boolean canShowTag() {
        return set.contains( Option.SHOW_TAG_FILTER );
    }

    public boolean isBusinessViewActive() {
        return set.contains( Option.BUSINESS_CONTENT );
    }

    public boolean isTechnicalViewActive() {
        return set.contains( Option.TECHNICAL_CONTENT );
    }

    public boolean isTreeNavigatorVisible() {
        return set.contains( Option.TREE_NAVIGATOR );
    }

    public void add( Option option ) {
        set.add( option );
    }

    public boolean contains( Option option ) {
        return set.contains( option );
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public Collection<Option> getValues() {
        return set;
    }

    public void addAll( Option... options ) {
        for (Option option : options) {
            set.add( option );
        }
    }

    public void clear() {
        set.clear();
    }

    public void addAll( Set<Option> optionSet ) {
        set.addAll( optionSet );
    }

    public boolean remove( Option treeNavigator ) {
        return set.remove( treeNavigator );
    }
}
