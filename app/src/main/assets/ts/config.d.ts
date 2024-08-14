import { CSSDeclaration, OneDArray, TColumn, TData } from './types';
import Storage from './storage';
import Pipeline from './pipeline';
import Tabular from './tabular';
import { SearchConfig } from './search';
import { PaginationConfig } from './pagination';
import Header from './header';
import { ServerStorageOptions } from './server';
import { GenericSortConfig } from './sort';
import { Language, Translator } from './src/i18n/language';
import { ComponentChild, RefObject } from 'preact';
import { EventEmitter } from './eventEmitter';
import { GridEvents } from './events';
import { PluginManager, Plugin } from './plugin';
import Grid from './grid';
import { Store } from './store';
export declare const ConfigContext: import("preact").Context<any>;
export interface Config {
    instance: Grid;
    store: Store;
    eventEmitter: EventEmitter<GridEvents>;
    plugin: PluginManager;
    container?: Element;
    tableRef?: RefObject<HTMLTableElement>;
    data?: TData | (() => TData) | (() => Promise<TData>);
    server?: ServerStorageOptions;
    header?: Header;
    from: HTMLElement;
    storage: Storage<any>;
    processingThrottleMs: number;
    pipeline: Pipeline<Tabular>;
    autoWidth: boolean;
    width: string;
    height: string;
    pagination: PaginationConfig | boolean;
    sort: GenericSortConfig | boolean;
    translator: Translator;
    fixedHeader: boolean;
    resizable: boolean;
    columns: OneDArray<TColumn | string | ComponentChild>;
    search: SearchConfig | boolean;
    language: Language;
    plugins?: Plugin<any>[];
    style?: Partial<{
        table: CSSDeclaration;
        td: CSSDeclaration;
        th: CSSDeclaration;
        container: CSSDeclaration;
        header: CSSDeclaration;
        footer: CSSDeclaration;
    }>;
    className?: Partial<{
        table: string;
        th: string;
        thead: string;
        tbody: string;
        tr: string;
        td: string;
        container: string;
        footer: string;
        header: string;
        search: string;
        sort: string;
        pagination: string;
        paginationSummary: string;
        paginationButton: string;
        paginationButtonNext: string;
        paginationButtonCurrent: string;
        paginationButtonPrev: string;
        loading: string;
        notfound: string;
        error: string;
    }>;
}
export declare class Config {
    constructor();
    assign(partialConfig: Partial<Config>): Config;
    update(partialConfig: Partial<Config>): Config;
    static defaultConfig(): Partial<Config>;
    static fromPartialConfig(partialConfig: Partial<Config>): Partial<Config>{
        if (config.pagination) {
            // Pagination
            config.plugin.remove('pagination');
            config.plugin.add({
              id: 'pagination',
              position: PluginPosition.Footer,
              component: Pagination,
            });
          }
          // Additional plugins
          if (config.plugins) {
            config.plugins.forEach((p) => config.plugin.add(p));
            config.plugins.forEach((p) => {config.plugin.remove(p.id);config.plugin.add(p);});
          }
    };
    
}

