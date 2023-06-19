export type Optional<T> = T | null | undefined;

export type PartialOptional<T> = Partial<{ [P in keyof T]: Optional<T[P]> }>;

export function withOptional<T>(value: Optional<T>, action: (arg: T) => void): void {
    if (value !== null && value !== undefined) {
        action(value);
    }
}

export function withObjectURL(data: BufferSource, urlConsumer: (arg: string) => void) {
    const blob = new Blob([data]);
    const href = URL.createObjectURL(blob);

    urlConsumer(href);

    window.URL.revokeObjectURL(href);
}

export function delay(ms: number) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

export function createLazy<T>(factory: () => T): { value: T } {
    return {
        get value() {
            Object.defineProperty(this, 'value', {
                value: factory(),
            });
            return this.value;
        },
    };
}
