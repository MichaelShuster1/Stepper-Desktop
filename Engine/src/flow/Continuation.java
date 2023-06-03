package flow;

import exception.ContinuationException;
import javafx.util.Pair;
import step.Step;

import java.util.*;

public class Continuation {
    private final String targetFlow;
    private Map<String, List<String>> oldFlow2NewForcedMapping;

    private Map<Pair<Integer,String>,List<Pair<Integer,String>>> continuationMapping;

    private Map<Pair<Integer,String>,List<Pair<Integer,String>>> continuationForcedMapping;

    public Continuation(String targetFlow) {
        this.targetFlow = targetFlow;
        this.oldFlow2NewForcedMapping = new HashMap<>();
    }

    public void addForcedConnection(String from,String to) {
        if(oldFlow2NewForcedMapping.containsKey(from)) {
            if (!oldFlow2NewForcedMapping.get(from).contains(to))
                oldFlow2NewForcedMapping.get(from).add(to);
        }
        else {
            List<String> list = new ArrayList<>();
            list.add(to);
            oldFlow2NewForcedMapping.put(from,list);
        }
    }

    public Map<Pair<Integer, String>, List<Pair<Integer, String>>> getContinuationMapping() {
        return continuationMapping;
    }

    public Map<Pair<Integer, String>, List<Pair<Integer, String>>> getContinuationForcedMapping() {
        return continuationForcedMapping;
    }

    public String getTargetFlow() {
        return targetFlow;
    }

    public Map<String, List<String>> getOldFlow2NewForcedMapping() {
        return oldFlow2NewForcedMapping;
    }

    public void createContinuation(Flow sourceFlow, Flow targetFlow) {
        continuationMapping = new HashMap<>();
        continuationForcedMapping = new HashMap<>();
        List<Step> sourceSteps = sourceFlow.getSteps();
        List<Step> targetSteps = targetFlow.getSteps();
        Map<String, List<Integer>> targetFreeInputs = targetFlow.getFlowFreeInputs();
        Map<String, Integer> sourceOutputs = sourceFlow.getFlowOutputs();
        Map<String, List<Integer>> sourceFreeInputs = sourceFlow.getFlowFreeInputs();
        Set<String> forcedAssignedInputs = null;
        if (oldFlow2NewForcedMapping.size() != 0) {
           forcedAssignedInputs =  doForcedMapping(sourceSteps, targetSteps, targetFreeInputs, sourceOutputs, sourceFreeInputs);
        }
        doMapping(sourceSteps, targetSteps, targetFreeInputs, sourceOutputs, sourceFreeInputs, forcedAssignedInputs);

    }

    private void doMapping(List<Step> sourceSteps, List<Step> targetSteps, Map<String, List<Integer>> targetFreeInputs, Map<String, Integer> sourceOutputs, Map<String, List<Integer>> sourceFreeInputs, Set<String> forcedAssignedInputs) {
        for(String sourceName: sourceFreeInputs.keySet()) {
            addMapping(sourceSteps, targetSteps, targetFreeInputs, sourceFreeInputs, forcedAssignedInputs, sourceName, false);
        }
        for(String sourceName : sourceOutputs.keySet()) {
            addMapping(sourceSteps, targetSteps, targetFreeInputs, sourceFreeInputs, forcedAssignedInputs, sourceName, true);
        }
    }

    private void addMapping(List<Step> sourceSteps, List<Step> targetSteps, Map<String, List<Integer>> targetFreeInputs, Map<String, List<Integer>> sourceFreeInputs, Set<String> forcedAssignedInputs, String sourceName, boolean flag) {
        if(targetFreeInputs.containsKey(sourceName)) {
            if(!(forcedAssignedInputs != null && forcedAssignedInputs.contains(sourceName))) {
                List<Integer> allCurrFreeInputs = targetFreeInputs.get(sourceName);
                Pair<Integer,String> sourcePair = new Pair<>(sourceFreeInputs.get(sourceName).get(0), sourceName);
                if(!continuationMapping.containsKey(sourcePair)) {
                    List<Pair<Integer,String>> newList = new ArrayList<>();
                    continuationMapping.put(sourcePair,newList);
                }

                for(Integer currIndex : allCurrFreeInputs) {
                    Pair<Integer,String> targetPair = new Pair<>(currIndex, sourceName);
                    String sourceType;
                    if(flag)
                        sourceType = sourceSteps.get(sourcePair.getKey()).getOutputByName(sourcePair.getValue()).getType();
                    else
                        sourceType = sourceSteps.get(sourcePair.getKey()).getInputByName(sourcePair.getValue()).getType();
                    if(sourceType.equals(targetSteps.get(targetPair.getKey()).getInputByName(targetPair.getValue()).getType()))
                        continuationMapping.get(sourcePair).add(targetPair);
                }
            }
        }
    }

    private Set <String> doForcedMapping(List<Step> sourceSteps, List<Step> targetSteps, Map<String, List<Integer>> targetFreeInputs, Map<String, Integer> sourceOutputs, Map<String, List<Integer>> sourceFreeInputs) {
        List<String> forcedTargets;
        Set<String> forcedAssignedInputs = new HashSet<>();
        for (String sourceName : oldFlow2NewForcedMapping.keySet()) {
            forcedTargets = oldFlow2NewForcedMapping.get(sourceName);
            if (sourceOutputs.containsKey(sourceName)) {
                for (String targetName : forcedTargets) {
                    if (targetFreeInputs.containsKey(targetName)) {
                        Pair<Integer,String> sourcePair = new Pair<>(sourceOutputs.get(sourceName),sourceName);
                        addForcedMapping(targetFreeInputs, targetName, sourcePair, sourceSteps, targetSteps, true);
                        forcedAssignedInputs.add(targetName);
                    } else
                        throw new ContinuationException("Continuation contains mapping to a target input that doesnt exists or contains an initial value, target input: " + targetName);
                }
            }
            else if (sourceFreeInputs.containsKey(sourceName)) {
                for (String targetName : forcedTargets) {
                    if (targetFreeInputs.containsKey(targetName)) {
                        Pair<Integer,String> sourcePair = new Pair<>(sourceFreeInputs.get(sourceName).get(0),sourceName);
                        addForcedMapping(targetFreeInputs,targetName,sourcePair, sourceSteps, targetSteps, false);
                        forcedAssignedInputs.add(targetName);
                    } else
                        throw new ContinuationException("Continuation contains mapping to a target input that doesnt exists or contains an initial value, target input: " + targetName);
                }
            }
            else
                throw new ContinuationException("Continuation contains mapping to a source data that doesnt exists, source data: " + sourceName);
            }

        return forcedAssignedInputs;
    }

    private void addForcedMapping(Map<String,List<Integer>> targetFreeInputs, String targetName, Pair<Integer, String> sourcePair, List<Step> sourceSteps, List<Step> targetSteps, boolean flag) {
        List<Integer> allCurrFreeInputs = targetFreeInputs.get(targetName);
        if(!continuationForcedMapping.containsKey(sourcePair)) {
            List<Pair<Integer,String>> newList = new ArrayList<>();
            continuationForcedMapping.put(sourcePair,newList);
        }

        for(Integer currIndex : allCurrFreeInputs) {
            Pair<Integer,String> targetPair = new Pair<>(currIndex, targetName);
            String sourceType;
            if(flag)
                sourceType = sourceSteps.get(sourcePair.getKey()).getOutputByName(sourcePair.getValue()).getType();
            else
                sourceType = sourceSteps.get(sourcePair.getKey()).getInputByName(sourcePair.getValue()).getType();

            if(!sourceType.equals(targetSteps.get(targetPair.getKey()).getInputByName(targetPair.getValue()).getType()))
                throw new ContinuationException("Flow continuation contains mapping for source data:" + sourcePair.getValue() + " to target data:" +
                        targetPair.getValue() +" while they have different data types");
            continuationForcedMapping.get(sourcePair).add(targetPair);
        }
    }
}


