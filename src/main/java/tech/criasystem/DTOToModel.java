package tech.criasystem;

import org.springframework.beans.BeanUtils;

public abstract class DTOToModel {

	public void dtoToModel(Object dto) {
		BeanUtils.copyProperties(dto, this);
	}
}
