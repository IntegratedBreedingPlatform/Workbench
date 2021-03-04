export class Tool {
    constructor(
        public name: string,
        public children: ToolLink[]
    ) {
    }
}

export class ToolLink {
    constructor(
        public name: string,
        public link: string,
    ) {
    }
}
