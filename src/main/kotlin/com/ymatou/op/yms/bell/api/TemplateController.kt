package com.ymatou.op.yms.bell.api

import com.ymatou.op.yms.bell.domain.Template
import com.ymatou.op.yms.bell.exception.NotFoundException
import com.ymatou.op.yms.bell.service.ExecuteService
import com.ymatou.op.yms.bell.service.TemplateService
import com.ymatou.op.yms.bell.template.Variable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Created by xuemingli on 2016/10/13.
 */
@RestController
@RequestMapping("/v1/template")
open class TemplateController {
    @Autowired
    private lateinit var templateService: TemplateService

    @Autowired
    private lateinit var executeService: ExecuteService

    @PostMapping("")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun create(@RequestBody template: Template): Template {
        return templateService.create(template)
    }

    @GetMapping("")
    @ResponseBody
    open fun list(@RequestParam(required = false, defaultValue = "1") page: Int,
             @RequestParam(required = false, defaultValue = "50") size: Int): PagedListResponse<Template, Template> {
        return PagedListResponse(page, templateService.getAll(page, size))
    }

    @GetMapping("/{id}")
    @ResponseBody
    open fun find(@PathVariable id: Long): Template {
        return templateService.get(id) ?: throw NotFoundException("template $id not fund")
    }

    @PutMapping("/{id}")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun update(@RequestBody template: Template): Template {
        return templateService.update(template)
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @PreAuthorize("@securityService.hasRole('admin', 'ops')")
    open fun delete(@PathVariable id: Long): Template? {
        return templateService.delete(id)
    }

    @GetMapping("/search", params = arrayOf("q"))
    @ResponseBody
    open fun search(@RequestParam("q") q: String): List<Template> {
        return templateService.search(q)
    }

    @GetMapping("/vars", params = arrayOf("name"))
    @ResponseBody
    open fun vars(@RequestParam("name") name: String): Map<String, Variable> {
        val template = templateService.findByName(name) ?:  throw NotFoundException("template $name not fund")
        return executeService.variables(template).get()
    }
}