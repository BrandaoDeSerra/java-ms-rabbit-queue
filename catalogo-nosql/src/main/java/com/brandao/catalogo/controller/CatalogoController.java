package com.brandao.catalogo.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brandao.catalogo.data.vo.CatalogoVO;
import com.brandao.catalogo.services.CatalogoService;;

@RestController
@RequestMapping("/catalogo")
public class CatalogoController {

	private final CatalogoService catalogoService;
	private final PagedResourcesAssembler<CatalogoVO> assembler;
	
	@Autowired
	public CatalogoController(CatalogoService catalogoService, PagedResourcesAssembler<CatalogoVO> assembler) {
		this.catalogoService = catalogoService;
		this.assembler = assembler;
	}
	
	@GetMapping(value = "/{id}", produces = {"application/json","application/xml","application/x-yaml"})
	public CatalogoVO findById(@PathVariable("id")  String id) {
		CatalogoVO catalogoVO = catalogoService.findById(id);
		catalogoVO.add(linkTo(methodOn(CatalogoController.class).findById(id)).withSelfRel());
		return catalogoVO;
	}
	
	@GetMapping(produces = {"application/json","application/xml","application/x-yaml"})
	public ResponseEntity<?> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {
		
		var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		
		Pageable pageable = PageRequest.of(page,limit, Sort.by(sortDirection,"nome"));
		
		Page<CatalogoVO> produtos = catalogoService.findAll(pageable);
		produtos.stream()
				.forEach(p -> p.add(linkTo(methodOn(CatalogoController.class).findById(p.getId())).withSelfRel()));
		
		PagedModel<EntityModel<CatalogoVO>> pagedModel = assembler.toModel(produtos);
		
		return new ResponseEntity<>(pagedModel,HttpStatus.OK);
	}
	
	@PostMapping(produces = {"application/json","application/xml","application/x-yaml"}, 
			     consumes = {"application/json","application/xml","application/x-yaml"})
	public CatalogoVO create(@RequestBody CatalogoVO objVO) {
		CatalogoVO proVo = catalogoService.create(objVO);
		proVo.add(linkTo(methodOn(CatalogoController.class).findById(proVo.getId())).withSelfRel());
		return proVo;
	}
	
	@PutMapping(produces = {"application/json","application/xml","application/x-yaml"}, 
		     consumes = {"application/json","application/xml","application/x-yaml"})
	public CatalogoVO update(@RequestBody CatalogoVO objVO) {
		CatalogoVO proVo = catalogoService.update(objVO);
		proVo.add(linkTo(methodOn(CatalogoController.class).findById(objVO.getId())).withSelfRel());
		return proVo;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") String id){
		catalogoService.delete(id);
		return ResponseEntity.ok().build();
	}
}













