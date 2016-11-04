/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.RegistroVenta;
import com.losalpes.excepciones.CupoInsuficienteException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author dito
 */
@Remote
public interface IPersistenciaBMTRemote {
    public void comprar(List<RegistroVenta> compra) throws CupoInsuficienteException;
}
