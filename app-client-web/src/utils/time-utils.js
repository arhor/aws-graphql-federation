function int(num) {
    return num | 0;
}

export function secondsToMillis(num) {
    return int(int(num) * MILLIS_IN_1_SECOND);
}

export const MILLIS_IN_1_SECOND = int(1000);
export const MILLIS_IN_5_SECONDS = secondsToMillis(5);
export const MILLIS_IN_10_SECONDS = secondsToMillis(10);
