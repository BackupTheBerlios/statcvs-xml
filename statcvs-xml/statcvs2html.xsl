<?xml version="1.0" encoding="UTF-8"?>

<!--
   A simple XSL file from Tammo van Lessen
   Transforms Commitlog to HTML
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:i18n="net.sf.statcvs.I18n"
				xmlns:prefs="net.sf.statcvs.output.xml.OutputSettings"
				xmlns:fileutils="net.sf.statcvs.util.FileUtils">

  <xsl:output method="html" indent="yes" encoding="ISO-8859-1"/>
  <xsl:param name="ext"/>
  <xsl:include href="statcvs2xdoc.xsl"/>

  <xsl:template match="document">
    <html>
       <head>
         <title><xsl:value-of select="@title"/></title>
		 <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
		 <meta name="Generator" content="StatCvs-XML v0.1-dev"/>
	     <link rel="stylesheet" href="{fileutils:getFilenameWithoutPath(prefs:getCss())}" type="text/css"/>
       </head>
       <body>
	     <h1><xsl:value-of select="@title"/></h1>
	     <xsl:apply-templates select="report"/>
       </body>
    </html>
  </xsl:template>

  <xsl:template match="report">
    <h2><xsl:value-of select="@name"/></h2>
	<xsl:apply-templates select="*"/>
  </xsl:template>

</xsl:stylesheet>
