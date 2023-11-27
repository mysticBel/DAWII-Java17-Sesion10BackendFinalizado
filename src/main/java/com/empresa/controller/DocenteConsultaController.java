package com.empresa.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.entity.Docente;
import com.empresa.service.DocenteService;
import com.empresa.util.Constantes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@RestController
@RequestMapping("/url/consultaDocente")
@CrossOrigin(origins = "http://localhost:4200")
@CommonsLog  //barras
public class DocenteConsultaController {

	@Autowired
	private DocenteService docenteService;
	
	//Creando consulta, que se envien parametros por ruta
	// opcionales no obligatorios
	@ResponseBody
	@GetMapping("/consultaDocentePorParametros")
	public List<Docente> listaConsultaDocente(
			@RequestParam(name = "nombre" , required = false , defaultValue = "") String nombre, 
			@RequestParam(name = "dni" , required = false , defaultValue = "") String dni, 
			@RequestParam(name = "estado" , required = false , defaultValue = "1") int estado, 
			@RequestParam(name = "idUbigeo" , required = false , defaultValue = "-1") int idUbigeo){
		List<Docente> lstSalida = docenteService.listaConsulta("%"+nombre+"%", dni, estado, idUbigeo);
		return lstSalida;
	}
	
	
	// Para el reporte PDF - w11
	@GetMapping("/reporteDocentePdf")
	public void  exportaPDF(
			@RequestParam(name = "nombre" , required = false , defaultValue = "") String nombre, 
			@RequestParam(name = "dni" , required = false , defaultValue = "") String dni, 
			@RequestParam(name = "estado" , required = false , defaultValue = "1") int estado, 
			@RequestParam(name = "idUbigeo" , required = false , defaultValue = "-1") int idUbigeo,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		try {
			//PASO 1 Fuente de datos DataSource
			List<Docente> lstSalida = docenteService.listaConsulta("%"+nombre+"%", dni, estado, idUbigeo);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lstSalida);  //jasper reports
			
			//PASO 2  reporte
			String fileReporte = request.getServletContext().getRealPath("/WEB-INF/reportes/ReporteDocente.jasper");
			log.info(">>>>>>> fileReporte >> "+ fileReporte);
			
			//PASO 3 parametros adicionales	
			// loguito
			String fileLogo  = request.getServletContext().getRealPath("/WEB-INF/img/logo.jpg");
			log.info(">>> fileLogo >> " + fileLogo);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("RUTA_LOGO", fileLogo);
			
			//PASO 4 Se juntan la data , diseño y parametros
			JasperReport jasperReport =(JasperReport)JRLoader.loadObject(new FileInputStream(new File(fileReporte)));
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, dataSource);
				
			//PASO 5 paramtros en el header  del mensajes HTTP
			response.setContentType("application/pdf");
			response.addHeader("content-disposition", "attachment, filename=ReporteDocente");
			
			//PASO 6 Se envia el reporte
			OutputStream os = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, os);
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}
	
	//REPORTE EN EXCEL
	private static String[] HEADERs = {"CÓDIGO", "NOMBRE", "DNI", "ESTADO","UBIGEO", "FECHA REGISTRO"};
	private static String SHEET = "Listado";
	private static String TITLE = "Listado de docentes - Autor: Maribel Maza";
	private static int[] HEADER_WITH = { 3000, 10000, 6000, 10000, 20000, 10000 };
	
	@PostMapping("/reporteDocenteExcel")
	public void  exportaExcel(
			@RequestParam(name = "nombre" , required = false , defaultValue = "") String nombre, 
			@RequestParam(name = "dni" , required = false , defaultValue = "") String dni, 
			@RequestParam(name = "estado" , required = false , defaultValue = "1") int estado, 
			@RequestParam(name = "idUbigeo" , required = false , defaultValue = "-1") int idUbigeo,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		Workbook excel = null;
		try {
			//Se crea el excel
			excel  = new XSSFWorkbook();
			
			// Se crea la hoja de Excel
			Sheet hoja = excel.createSheet(SHEET);
			
			//
			response.setContentType(Constantes.TYPE_EXCEL);
    	    response.addHeader("Content-disposition", "attachment; filename=ReporteDocente.xlsx");
    	    
			
					
					
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
