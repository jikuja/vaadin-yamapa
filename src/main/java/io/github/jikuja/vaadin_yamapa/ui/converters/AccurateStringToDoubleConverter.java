package io.github.jikuja.vaadin_yamapa.ui.converters;

import com.vaadin.data.util.converter.StringToDoubleConverter;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Converter from String to Double. Conversion uses 5 decimal precision => 1.1 meters
 */
public class AccurateStringToDoubleConverter extends StringToDoubleConverter {
    @Override
    protected NumberFormat getFormat(Locale locale) {
        NumberFormat format = super.getFormat(locale);
        format.setMinimumFractionDigits(5);
        format.setMaximumFractionDigits(5);
        return format;
    }
}
