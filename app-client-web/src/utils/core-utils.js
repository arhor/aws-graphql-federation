/**
 * @param {BufferSource} data 
 * @param {(arg: string) => void} urlConsumer 
 */
export function withObjectURL(data, urlConsumer) {
    const blob = new Blob([data]);
    const href = URL.createObjectURL(blob);

    try {
        urlConsumer(href);
    } finally {
        window.URL.revokeObjectURL(href);
    }
}

/**
 * @param {number} ms 
 * @returns 
 */
export function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * @template T
 * @param {() => T} factory 
 * @returns {{ value: T }}
 */
export function createLazy(factory) {
    return {
        get value() {
            Object.defineProperty(this, 'value', {
                value: factory(),
            });
            return this.value;
        },
    };
}
