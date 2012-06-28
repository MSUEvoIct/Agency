package abce.agency.test.events;


import abce.agency.events.EventProcedureDescription;
import abce.agency.events.ValuedEventProcedure;
import abce.agency.util.BadConfiguration;



public class TestValuedEventProcedure extends ValuedEventProcedure {

	public TestValuedEventProcedure(byte type, EventProcedureDescription desc) throws BadConfiguration {
		super(type, desc);
		// TODO Auto-generated constructor stub
	}



	@Override
	public void execute(Object... context) throws Exception {
		if (this._action == null) {
			_action = _action_class.newInstance();
			_action.setup(this._arguments);
		}
		_action.process(context);
	}

}
