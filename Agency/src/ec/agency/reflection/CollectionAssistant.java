package ec.agency.reflection;


import java.util.Collection;



public class CollectionAssistant<X> extends Assistant {

	@Stimulus(name = "Iterate", assistant = CollectionIteratorAssistant.class)
	public StatefulIterator<X> iterate(Collection<X> in) {
		return new StatefulIterator<X>(in.iterator());
	}

}
