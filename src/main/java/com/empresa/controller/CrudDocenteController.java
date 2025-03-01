package com.empresa.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.entity.Docente;
import com.empresa.service.DocenteService;
import com.empresa.util.Constantes;

@RestController
@RequestMapping("/url/crudDocente")
@CrossOrigin(origins = "http://localhost:4200")
public class CrudDocenteController {

	@Autowired
	private DocenteService service;
	
	
	@GetMapping("/listaDocentePorNombreLike/{nom}")
	@ResponseBody
	public ResponseEntity<List<Docente>> listaDocentePorNombreLike(@PathVariable("nom") String nom) {
		List<Docente> lista  = null;
		try {
			if (nom.equals("todos")) {
				lista = service.listaDocentePorNombreLike("%");
			}else {
				lista = service.listaDocentePorNombreLike("%" + nom + "%");	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(lista);
	}
	
	@PostMapping("/registraDocente")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> insertaDocente(@RequestBody Docente obj) {
		Map<String, Object> salida = new HashMap<>();
		try {
			obj.setIdDocente(0);
			obj.setFechaRegistro(new Date());
			//Descomentar para la PC02
			//obj.setFechaActualizacion(new Date());
			obj.setEstado(1);
			
			//Validación de Nombre unique
			List<Docente> lstDocenteNombre =  service.listaPorNombreIgualRegistra(obj.getNombre());
			if (!lstDocenteNombre.isEmpty()) {
				salida.put("mensaje", "El Docente " + obj.getNombre() + " ya existe");
				return ResponseEntity.ok(salida);
			}
			
			//Validación de DNI unique
			List<Docente> lstDocenteUnique =  service.listaPorDNIIgualRegistra(obj.getDni());
			if (!lstDocenteUnique.isEmpty()) {
				salida.put("mensaje", "El DNI " + obj.getDni() + " ya existe");
				return ResponseEntity.ok(salida);
			}
			

			
			Docente objSalida =  service.insertaActualizaDocente(obj);
			if (objSalida == null) {
				salida.put("mensaje", Constantes.MENSAJE_REG_ERROR);
			} else {
				salida.put("mensaje", Constantes.MENSAJE_REG_EXITOSO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			salida.put("mensaje", Constantes.MENSAJE_REG_ERROR);
		}
		return ResponseEntity.ok(salida);
	}

	@PutMapping("/actualizaDocente")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> actualizaDocente(@RequestBody Docente obj) {
		Map<String, Object> salida = new HashMap<>();
		
		//Descomentar para la PC02
		//obj.setFechaActualizacion(new Date());
		
		//Validación de Nombre unique
		List<Docente> lstDocenteNombre =  service.listaPorNombreIgualActualiza(obj.getNombre(), obj.getIdDocente());
		if (!lstDocenteNombre.isEmpty()) {
			salida.put("mensaje", "El Docente " + obj.getNombre() + " ya existe");
			return ResponseEntity.ok(salida);
		}
		
		//Validación de DNI unique
		List<Docente> lstDocenteUnique =  service.listaPorDNIIgualActualiza(obj.getDni(), obj.getIdDocente());
		if (!lstDocenteUnique.isEmpty()) {
			salida.put("mensaje", "El DNI " + obj.getDni() + " ya existe");
			return ResponseEntity.ok(salida);
		}
		
		try {
			Docente objSalida =  service.insertaActualizaDocente(obj);
			if (objSalida == null) {
				salida.put("mensaje", Constantes.MENSAJE_ACT_ERROR);
			} else {
				salida.put("mensaje", Constantes.MENSAJE_ACT_EXITOSO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			salida.put("mensaje", Constantes.MENSAJE_ACT_ERROR);
		}
		return ResponseEntity.ok(salida);
	}
	
	
	@DeleteMapping("/eliminaDocente/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> eliminaDocente(@PathVariable("id") int idDocente) {
		Map<String, Object> salida = new HashMap<>();
		try {
			service.eliminaDocente(idDocente);
			salida.put("mensaje", Constantes.MENSAJE_ELI_EXITOSO);
		} catch (Exception e) {
			e.printStackTrace();
			salida.put("mensaje", Constantes.MENSAJE_ELI_ERROR);
		}
		return ResponseEntity.ok(salida);
	}
	
}







