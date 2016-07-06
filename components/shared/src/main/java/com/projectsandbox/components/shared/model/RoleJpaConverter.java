package com.projectsandbox.components.shared.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by limpygnome on 04/07/16.
 */
@Converter
public class RoleJpaConverter implements AttributeConverter<Role, Integer>
{

    @Override
    public Integer convertToDatabaseColumn(Role role)
    {
        return role.ID;
    }

    @Override
    public Role convertToEntityAttribute(Integer value)
    {
        Role role = null;

        for (Role possibleRole : Role.values())
        {
            if (possibleRole.ID == value)
            {
                role = possibleRole;
                break;
            }
        }

        return role;
    }

}
