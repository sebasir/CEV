package net.hpclab.cev.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class BooleanToCharConverter implements AttributeConverter<Boolean, Character> {

	@Override
	public Character convertToDatabaseColumn(Boolean yesOrNo) {
		return yesOrNo ? 'S' : 'N';
	}

	@Override
	public Boolean convertToEntityAttribute(Character columnValue) {
		return columnValue == 'S';
	}

}
