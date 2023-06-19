import { Optional, PartialOptional } from '@/utils/core-utils';

export type ValidationRule<T> = (v: Optional<T>) => true | string;

export type ValidationRules<T> = Partial<{ [P in keyof T]: ValidationRule<T[P]>[] }>;

export type ValidationErrors<T> = Partial<{ [P in keyof T]: string }>;

export type ValidationFunction<T> = (values: PartialOptional<T>) => ValidationErrors<T>;

export function defineValidator<T>(rules: ValidationRules<T>): ValidationFunction<T> {
    return (values: PartialOptional<T>) => {
        const errors: ValidationErrors<T> = {};

        if (values) {
            for (const field in values) {
                const fieldRules = rules[field] as Optional<ValidationRule<T[keyof T]>[]>;

                if ((fieldRules !== null) && (fieldRules !== undefined)) {
                    for (const rule of fieldRules) {
                        const value = values[field];
                        const success = rule(value);

                        if (success !== true) {
                            errors[field] = success;
                            break;
                        }
                    }
                }
            }
        }
        return errors;
    };
}

export function formIsValid<T>(errors: ValidationErrors<T>): boolean {
    for (const error in Object.values(errors)) {
        if (error) {
            return false;
        }
    }
    return true;
}
