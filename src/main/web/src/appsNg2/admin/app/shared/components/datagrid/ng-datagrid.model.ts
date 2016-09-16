import './../../utils/object.extensions';

export class NgDataGridModel<T> {
    searchValue: T = <T>{};
    sortBy: string;
    sortAsc: boolean = true;
    currentPageIndex: number = 1;
    pageSize: number;
    private _items: T[];

    constructor(items: T[], pageSize: number = 10) {
        this._items = items;
        this.pageSize = pageSize;
    }

     ngOnInit(){
       
    }
    
    get totalRows(): number {
        return this._items.length;
    }

    get totalFilteredRows(): number {
        return this.itemsFiltered.length;
    }

    private get currentRowStart(): number {
        return this.totalRows > this.pageSize
            ? (this.startRow + 1)
            : this.totalRows === 0 ? 0 : 1;
    }
    private get currentRowEnd(): number {
        return (this.startRow + this.pageSize) < this.totalRows
            ? (this.startRow + this.pageSize)
            : this.totalRows;
    }

    get startRow(): number {
        if (this.currentPageIndex === 0)
            return 0;

        return (this.currentPageIndex - 1) * this.pageSize;
    }
    get maxPageIndex(): number {
        let index = Math.ceil(this.totalFilteredRows / this.pageSize);
        return index;
    }

    set items(value: T[]) {
        this._items = value;
    }
    get items(): T[] {
        return this._items;
    }

    get itemsFiltered(): T[] {
        let key: string = this.sortBy;

        return this.items
            .filter(item => Object.same(this.searchValue, item))
            .sort((obj1: T, obj2: T) => {

                let one: number = this.sortAsc ? 1 : -1;

                if (obj1[key] < obj2[key]) return -one;
                if (obj1[key] > obj2[key]) return one;
                return 0;
            });
    }

    get itemsOnCurrentPage(): T[] {
        return this.itemsFiltered.slice(this.startRow,
            this.startRow + this.pageSize);
    }
    

    
}
