export function withObjectURL(data, urlConsumer) {
    const blob = new Blob([data]);
    const href = URL.createObjectURL(blob);

    try {
        urlConsumer(href);
    } finally {
        window.URL.revokeObjectURL(href);
    }
}

export function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

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
