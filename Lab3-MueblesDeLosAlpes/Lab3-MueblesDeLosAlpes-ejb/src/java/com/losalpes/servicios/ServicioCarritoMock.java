/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ServicioCarritoMock.java
 * Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.servicios;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.RegistroVenta;
import com.losalpes.entities.Usuario;
import com.losalpes.excepciones.CupoInsuficienteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 * Implementacion de los servicios del carrito de compras en el sistema.
 *
 */
@Stateless
public class ServicioCarritoMock implements IServicioCarritoMockRemote, IServicioCarritoMockLocal {
    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------

    /**
     * Interface con referencia al servicio de persistencia en el sistema
     */
    @EJB
    private IServicioPersistenciaMockLocal persistencia;

    @Resource
    private SessionContext context;

    /**
     * Lista con los muebles del carrito
     */
    private ArrayList<Mueble> inventario;

    /**
     * Precio total de todo el inventario
     */
    private double precioTotalInventario = 0.0;

    /**
     * Total de unidades en el inventario del carrito
     */
    private int totalUnidades = 0;

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor sin argumentos de la clase
     */
    public ServicioCarritoMock() {
        inventario = new ArrayList<Mueble>();
    }

    //-----------------------------------------------------------
    // Getters y setters
    //-----------------------------------------------------------
    /**
     * Devuelve el inventario de muebles que se encuentran en el carrito
     *
     * @return inventario Lista con los muebles que se encuentran en el carrito
     */
    @Override
    public ArrayList<Mueble> getInventario() {
        return inventario;
    }

    /**
     * Modifica el inventario del carrito
     *
     * @param inventario Nueva lista de muebles
     */
    @Override
    public void setInventario(ArrayList<Mueble> inventario) {
        this.inventario = inventario;
    }

    /**
     * Devuelve el precio total del inventario
     *
     * @return precioTotalInventario Precio total del inventario
     */
    @Override
    public double getPrecioTotalInventario() {
        return precioTotalInventario;
    }

    /**
     * Devuelve el cantidad total de unidades en el carrito
     *
     * @return totalUnidades Cantidad total de unidades en el carrito
     */
    @Override
    public int getTotalUnidades() {
        return totalUnidades;
    }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------
    /**
     * Realiza la compra de los items que se encuentran en el carrito
     *
     * @param usuario Usuario que realiza la compra
     */
    @Override
    public void comprar(Usuario usuario) {
        try {
            List<RegistroVenta> listaVentas = new ArrayList();
            Mueble mueble;
            //try {
            for (int i = 0; i < inventario.size(); i++) {
                mueble = inventario.get(i);
                Mueble editar = (Mueble) persistencia.findById(Mueble.class, mueble.getReferencia());
                editar.setCantidad(editar.getCantidad() - mueble.getCantidad());
                RegistroVenta compra = new RegistroVenta(new Date(System.currentTimeMillis()), mueble, mueble.getCantidad(), usuario.getCiudad().getNombre(), usuario);

                usuario.agregarRegistro(compra);

                persistencia.update(usuario);
                persistencia.update(editar);

                listaVentas.add(compra);

            }
            persistencia.comprar(listaVentas);
            System.out.println("Se va a limpiar la lista");
            limpiarLista();

        } catch (CupoInsuficienteException ex) {
            System.out.println("Se va a realizar el rollback controlado");
            context.setRollbackOnly();
            // Dito: Por qué no se ejecuta?
        }
    }

    /**
     * Agrega un nuevo mueble al carro de compras
     *
     * @param mueble Mueble que se agrega al carrito
     */
    @Override
    public void agregarItem(Mueble mueble) {
        boolean found = false;
        Mueble item;
        for (int i = 0, max = inventario.size(); i < max; i++) {
            item = (Mueble) inventario.get(i);
            if (item.getReferencia() == mueble.getReferencia()) {
                item.incrementarCantidad();
                found = true;
                break;
            }
        }

        // Si el item no se encuentra se agrega al inventario
        if (!found) {
            inventario.add(mueble);
            mueble.incrementarCantidad();
        }

        // Actualiza el inventario
        recalcularInventarioTotal();
    }

    /**
     * Remueve un mueble del carrito de compra
     *
     * @param mueble Mueble a remover
     * @param removerCero Indica si al ser cero se elimina de la lista
     */
    @Override
    public void removerItem(Mueble mueble, boolean removerCero) {

        Mueble foundItem = null;
        Mueble item;
        for (int i = 0, max = inventario.size(); i < max; i++) {
            item = (Mueble) inventario.get(i);
            if (item.getReferencia() == mueble.getReferencia()) {
                item.reducirCantidad();
                foundItem = item;
                break;
            }
        }

        // Remueve el item si la cantidad es menor o igual a cero
        if (removerCero && foundItem != null
                && foundItem.getCantidad() <= 0) {
            inventario.remove(foundItem);
        }

        // Actualiza el inventario
        recalcularInventarioTotal();
    }

    /**
     * Recalcula el costo y la cantidad de inventario
     */
    @Override
    public void recalcularInventarioTotal() {
        precioTotalInventario = 0;
        totalUnidades = 0;
        Mueble item;
        for (int i = 0, max = inventario.size(); i < max; i++) {
            item = (Mueble) inventario.get(i);
            precioTotalInventario += item.getPrecio() * item.getCantidad();
            totalUnidades += item.getCantidad();
        }
    }

    /**
     * Limpia el carrito de compras
     */
    @Override
    public void limpiarLista() {
        inventario.clear();
    }

}