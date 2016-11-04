/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.RegistroVenta;
import com.losalpes.excepciones.CupoInsuficienteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

/**
 *
 * @author dito
 */
// Por defecto la transaccion la maneja el contenedor
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PersistenciaCMT implements IPersistenciaCMTLocal {

    @Resource
    private SessionContext context;
    
    @Resource(mappedName = "jdbc/__default")
    private DataSource dataSource;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void comprar(List<RegistroVenta> compraTotal){
        System.out.println("Llego a servicio de compra por User");
        String sql = null;

        //List list = new ArrayList();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long documentoCliente = 0;
        double cupo = 0;
        double valorTotalCompra = 0;
        try {


            for (RegistroVenta compra : compraTotal) {
                valorTotalCompra = compra.getProducto().getPrecio() * compra.getCantidad();
                documentoCliente = compra.getComprador().getDocumento();
            }
            System.out.println("El valor total de la compra fue de:" + valorTotalCompra);
            
            // Propiedades para la conexion
//            Properties opciones_coneccion = new Properties();
//            opciones_coneccion.put("user", "app");
//            opciones_coneccion.put("password", "app");
            //Conectar con la base de datos
            //con = DriverManager.getConnection("jdbc:derby://localhost:1527/sample", opciones_coneccion);

            con = dataSource.getConnection();
            System.out.println("Schema conectado:" + con.getSchema());

            Statement stmt = con.createStatement();
            sql = "SELECT NUMERO, CUPO FROM APP.TARJETACREDITOALPES WHERE NUMERO=" + documentoCliente;
            System.out.println("Sentencia a ejecutar:" + sql);
            rs = stmt.executeQuery(sql);

            //rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("============CUSTOMER CON ID 1===========");
                System.out.println("Numero \t Cupo");
                System.out.println(rs.getInt(1) + "\t" + rs.getInt(2));
                cupo = rs.getInt(2);
            }

            if (valorTotalCompra > cupo) {
                throw new CupoInsuficienteException("No posee suficiente cupo en la tarjeta");
            }
            System.out.println("Se puede efecutar el tramite");
            
            
            sql = "UPDATE TARJETACREDITOALPES SET CUPO=? WHERE NUMERO=?";
            System.out.println("Sentencia a ejecutar:" + sql);
            
            PreparedStatement pstmtUpdate=con.prepareStatement(sql);
            pstmtUpdate.setDouble(1, cupo-valorTotalCompra);
            pstmtUpdate.setLong(2, documentoCliente);            
            pstmtUpdate.executeUpdate();
            
           
        } catch (Exception ex) {
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }

    }
    
   


}
