package abce.agency.reflection;


public class CollectionIteratorAssistant<X> {

	@Stimulus(name = "Next", assistant=CollectionIteratorAssistant.class)
	public StatefulIterator<X> next(StatefulIterator<X> it){
		it.next();
		return it;
	}
	
	@Stimulus(name = "Value")
	public X value(StatefulIterator<X> it){
		return it.value();
	}
}
