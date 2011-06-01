package messages

trait htmlRenderer {
	
	def renderHtml(): String
}

trait jsonRenderer {

	def renderJson(): String
}

trait xmlRenderer {

	def renderXml(): String
}

trait Renderer extends htmlRenderer with jsonRenderer with xmlRenderer
