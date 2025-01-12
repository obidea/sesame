<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE rdf:RDF [
   <!ENTITY xsd  "http://www.w3.org/2001/XMLSchema#" >
]>
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:sparql="http://www.w3.org/2005/sparql-results#" xmlns="http://www.w3.org/1999/xhtml">
   
   <xsl:include href="../locale/messages.xsl" />
   <xsl:variable name="title">
      <xsl:value-of select="$repository-create.title" />
   </xsl:variable>
   <xsl:include href="template.xsl" />
   <xsl:template match="sparql:sparql">
      <script src="../../scripts/create.js" type="text/javascript" />
      <form action="create" method="post">
         <table class="dataentry">
            <tbody>
               <tr>
                  <th>
                     <xsl:value-of select="$repository-type.label" />
                  </th>
                  <td>
                     <select id="type" name="type">
                        <option value="semantika-vrepo">Semantika Virtual RDF Repository</option>
                     </select>
                  </td>
               </tr>
               <tr>
                  <th>
                     <xsl:value-of select="$repository-id.label" />
                  </th>
                  <td>
                     <input type="text" id="id" name="Repository ID" size="16" value="semantika-repo" />
                  </td>
               </tr>
               <tr>
                  <th>
                     <xsl:value-of select="$repository-title.label" />
                  </th>
                  <td>
                     <input type="text" id="title" name="Repository title" size="16" />
                  </td>
               </tr>
               <tr>
                  <th>
                     <xsl:value-of select="$semantika-config.label" />
                  </th>
                  <td>
                     <input type="text" id="cfgpath" name="Configuration path" size="48" />
                  </td>
               </tr>
               <tr>
                  <td>
                     <input type="button" value="{$cancel.label}" style="float:right"
                            href="repositories" onclick="document.location.href=this.getAttribute('href')" />
                     <input id="create" type="button" value="{$create.label}"
                            onclick="checkOverwrite()" />
                  </td>
               </tr>
            </tbody>
         </table>
      </form>
   </xsl:template>
</xsl:stylesheet>
