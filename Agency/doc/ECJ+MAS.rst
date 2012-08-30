:Author: Matthew Rupp :Date: 12 July 2012

=====================
Multi-species ECJ+ABM
=====================


Most of the information about how the entire EC representation / SR / Agent
binding code works uses Oligopoly as the domain model example in the
explanations.


------------------
Configuration File
------------------

Parameterizing which agents get bound to which individuals happens in a
subpopulation's species's configuration.  For example, in the Oligopoly model,
firms are configured like:

  pop.subpop.0.species.ind = abce.agency.firm.ECJProdPriceFirm

Parameterizing which representations get particular SR objects happens in the
species configuration for a particular subpopulation's individual.  For example:

  pop.subpop.0.species.ind.tree.0.sr = abce.agency.firm.sr.ScaleFirmPriceSR

These parameters aren't actually used by the core EC system; they are referenced
by Problems created by the Evaluator.  Specifically, these values are retrieved
by static methods in the GPMASProblem class.



--------------------------------------------------
Binding Agents, Representations, StimulusResponses
--------------------------------------------------

This part of the code is still done in an ad-hoc manner in some places.

Agents that need to bind to a GPTree should implement the
agency.ec.ecj.ECJEvolvableAgent interface.  All this interface provides is a
way to register a particular GPIndividual with a particular agent along with
informing the system which StimulusResponses to use.  It's not horribly
complicated; the static getters in GPMASProblem help make this mapping easier.
On a side note, agent constructors should strive to take no arguments if
possible.  This makes reflection a whole lot easier and fits in with how ECJ
handles reflective object construction via the newInstance method.   Additional
setup can be done with a specific setup method that only the specific
domain-problem class (like OligopolyProblem) needs to worry about.  Otherwise,
we have to write specific getters for each type of agent/constructor signature
pairing used in the domain.

For the Oligopoly model, all the registration does is store a reference to the
EvolutionState, the execution thread number (for random number purposes, this
needs to be passed), the GPIndividual, and which StimulusResponses are used
with the ECJAgent (e.g. ECJProdPriceFirm).  To make this process more general,
if we want to change the EC backend for example, I made the hierarchy look like
this::

  Firm <--------------------
                           |      
  (ProducingPricingFirm) <-| --ECProdPriceFirm <---     
                           |                      |
  (EvolvableAgent) <-------                       | --ECJProdPriceFirm
                                                  |
  (ECJEvolvableAgent) <----------------------------

Anything in parentheses are interfaces.  I have it set up this way so we can
change the EC system associated with this model without having to completely
rip apart the workings of the agents.  Firm and ProducingPricingFirm are the
same as you have set them up.  They should have nothing to do with the EC side
of the system.  EvolvableAgent is an interface that provides the emit, addSR,
and getFitness (just a double); ECJEvolvableAgent provides the registration
components mentioned in the preceeding paragraph.  Because there are different
StimulusResponse types (pricing, production), I have the registration process
separate out the SR requests into different queues and send their classes to
the ECProdPriceFirm class for classification.  When it's time to emit SR (for
example, price() is called from the ECProdPriceFirm class), the overridden emit
method in ECJProdPriceFirm encapsulates the SR into an ECJ Problem and sends it
to the representation bound by the registration process in ECJProdPriceFirm.  

To summarize, a non-EC class should:

  * Have a empty constructor signature

To make it ECJ-friendly it should:

  * Use the abce.agency.ec.ecj.ECJEvolvableAgent interface for the EC side
    to see the domain model.
  * Use the abce.agency.ec.EvolvableAgent interface if we want to use the
    emit, SR classification, and getFitness system for interaction.  Only the
    getFitness method is exposed to ECJ in the domain Problem class. I will
    probably add the same method and signature to the ECJEvolvableAgent
    interface in order to keep which concerns are domain model / EC subsystem
    specific. (TODO)


--------------------------
Stimulus Response Creation
--------------------------

For the Oligopoly model, the SR object hierarchy looks like this::

  (StimulusResponse) <-- (MarketSimulationSR)
                             ^ |
                  ------------------------ |                  |
            (FirmPricingSR)           (FirmProductionSR)
                  ^                        ^ |                |
          ScaleFirmPriceSR          ScaleFirmProductionSR


Again, interfaces are in parentheses.  The StimulusResponse object is very
simple: it provides a single interface to get a MethodDictionary and is called
dictionary().  The first non-interface class in the relationship are the leaves
in the hierarchy.  This is because I wanted to make sure that whatever SR
instances were being passed to the EC system could be maximally flexible with
their setup and composition.  MarketSimulationSR doesn't do anything; the idea
behind it was that I thought we might need something specific to a market
simulation; I didn't know what and it seemed like a good idea at the time.  It
is currently empty.  FirmPricingSR and FirmProductionSR provide setup(...)
interfaces to feed the necessary information about the current state of the
simulation into their respective SR children.  For this problem domain they are
identical.  In the Simternet domain, though, we would want to pass specific
information about an area of investment interest to the SR for classification;
the setup method would allow us to be consistent about how all SR objects
associated with that type of investment decision would behave. 

The layer between the leaves and the MarketSimulationSR in the hierarchy is very
important for the reason just mentioned.  In the Oligopoly model, segregation
of which SR instances are associated with which actions happens because
ECPriceProdFirm segregates registered SR based on inheritance from that middle
layer.  When it's time to make pricing decisions, for example, the collection
of SR classes registered via ECPriceProdAgent::addSR are instantiated as
implementing FirmPricingSR are instantiated.  How specific SR are sent to which
(sub)representations are handled by the information stored during the
ECJProdPriceFirm register(...) process.
               
Adding particular Stimuli to a StimulusResponse object is easy; simply tag
whatever methods and members in the class should be accessible with a @Stimulus
tag to expose them to the SR registration system.  A static member (either
through assignment or static init code) should create a method dictionary by
passing the SR class into its constructor.  For the Oligopoly model, this is
achieved by creating RestrictedMethodDictionaries of depth 3 with a particular
set of allowed return types.  This is an area that could see  improvement since
right now I'm requiring RestrictedMethodDictionaries to be used in the EC
system simply because I need a way of creating a bounded set of method paths
during initialization and mutation (TODO).  The dictionary() method, inherited
from StimulusResponse, should return this method.  For ECJ, actions are simply
read from the SR instance when needed by looking for the @Action tag on a
method.


-------------------------
Evaluator / Problem Setup
-------------------------

The OligopolyProblem's object hierarchy looks like this::

  (CallableGroupProblemForm) <--
                               |
  Problem <------------------ MASProblem <-- GPMASProblem
                                              ^
                                              |
                                      OligopolyProblem

MAS in this case is "Multi-Agent Simulation".

CallableGroupProblemForm provides a means of sending EvaluationGroups (mentioned
in a previous email) to be evaluated by individual threads via the new
GroupedEvaluator.  

MASProblem provides a means of configuring where in the configuration file
specific information about the problem is being stored (e.g. in the oligopoly
parameter block) as well as where the configuration file for the domain
simulation resides (e.g. oligopoly.cfg).  It also implements the reset() and
setupForEvaluation() methods for the rest of the hierarchy as well as the
ability to clone the problem.

GPMASProblem provides code for preparing the population for binding with domain
instances (e.g. making sure all unbounded Stimulus nodes are bound to a
particular method path), as well as how to get each agent constructor (they
must use the ECJEvolvableAgent interface and be specified as shown earlier).
This class is also where stimulus response objects are identified via
reflection of classes specified by the other set of parameters mentioned at the
top of this document.

OligopolyProblem is where you want to look for specifics on how to bind
representations, SR, and agents.  Because this process is very domain specific,
it should probably be implemented independently for each model but could follow
the same pattern for the Oligopoly project.  As it currently stands:

  1.  A new simulation is created.  The signature here doesn't matter
      since we don't have a general process for parameterizing the
      simulation being used aside from having a specific Problem type.  This is
      an area we can improve on (TODO), but we would need to come up with a
      standard way to refer to the domain model agents.

  2.  The model is configured:
      a. Any overrides specified in EPSimpleEvolutionState via the
         domain_config_overrides are set (e.g. command line settings)
      b. The model is forced to (re)register all configuration settings.

  3.  The file manager is established; in Oligopoly a reference of the
      ECJ file manager is set in a member of the OligopolySimulation.

  4.  The model is initialized.  That is, there is an initialization
      method specified in the OligopolySimulation that goes through and
      makes use of the settings in the configuration file.
      **Prior to calling this method, nothing in the configuration file should be used for any purpose other than storage by the domain model**.
      This allows for the use of command line overrides to change the behavior
      of the domain model.

  5.  The model is populated.  For each individual passed to the problem
      via the EvaluationGroup:
        a.  A constructor is identified for that agent from the
        configuration
            settings.
        b.  The set of stimulus response classes associated with the
            ECJ subpopulation associated with the individual is
            retrieved.
        c.  The agent is created. d.  The EvolutionState, Individual,
        StimulusResponses, thread number,
            and subpopulation (required for SR emission) are
            registered via the ECJEvolvableAgent::register method.

  6.  The event system is initialized using information stored in the
      SimpleEvolutionState's domain_events member.  This particular member
      gets populated by events triggered in the ECJ-side of the system, storing
      information about the events there during the generation they are needed.
      This allows for us to have one events file for both the domain model and
      EC subsystem.



---------------------
Event System / Output
---------------------

Information about the event system and file output system should be available
from a previous email.  Let me know if you need more information.
