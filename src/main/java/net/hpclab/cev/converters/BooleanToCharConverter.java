/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Conversor que permite determinar un valor de tipo <tt>Char</tt> desde la base
 * de datos, y convertirlo a <tt>Boolean</tt>.
 * 
 * @author Sebasir
 * @since 1.0
 *
 */
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
