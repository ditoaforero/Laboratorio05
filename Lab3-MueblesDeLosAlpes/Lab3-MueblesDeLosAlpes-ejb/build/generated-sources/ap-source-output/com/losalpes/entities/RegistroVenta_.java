package com.losalpes.entities;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.Usuario;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-11-04T13:13:41")
@StaticMetamodel(RegistroVenta.class)
public class RegistroVenta_ { 

    public static volatile SingularAttribute<RegistroVenta, String> ciudad;
    public static volatile SingularAttribute<RegistroVenta, Usuario> comprador;
    public static volatile SingularAttribute<RegistroVenta, Mueble> producto;
    public static volatile SingularAttribute<RegistroVenta, Integer> cantidad;
    public static volatile SingularAttribute<RegistroVenta, Long> registro;
    public static volatile SingularAttribute<RegistroVenta, Date> fechaVenta;

}