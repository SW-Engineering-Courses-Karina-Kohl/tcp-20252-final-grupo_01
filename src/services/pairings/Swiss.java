package services.pairings;

import models.Match;
import models.competitors.Competitor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Swiss<T extends Competitor> implements Pairing<T> {

    private final int maxWins;
    private final int maxLosses;
    private final BiFunction<T, T, Match<T>> createMatch;

    //... Adicionar validação para maxWins e maxLosses
    public Swiss(int maxWins, int maxLosses, BiFunction<T, T, Match<T>> createMatch) {
        this.maxWins = maxWins;
        this.maxLosses = maxLosses;
        this.createMatch = createMatch;
    }

    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {
        List<Match<T>> newRound = generateNextRound(participants, flatten(previousRounds));

        if (newRound.isEmpty()) return previousRounds;

        List<List<Match<T>>> updated = new ArrayList<>(previousRounds);
        updated.add(newRound);
        return updated;
    }

    private List<Match<T>> flatten(List<List<Match<T>>> rounds) {
        return rounds.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<Match<T>> generateNextRound(List<T> participants, List<Match<T>> previousMatches) {
        List<Match<T>> newMatches = new ArrayList<>();

        Map<T, int[]> records = calculateRecords(participants, previousMatches);
        List<T> activePlayers = getActivePlayers(participants, records);
        Map<String, List<T>> buckets = groupByRecord(activePlayers, records);

        pairBuckets(buckets, newMatches);
        return newMatches;
    }

    private Map<T, int[]> calculateRecords(List<T> participants, List<Match<T>> matches) {
        Map<T, int[]> records = new HashMap<>();
        for (T p : participants) records.put(p, new int[]{0, 0});

        for (Match<T> m : matches) {
            if (m.getWinner() == null) continue;

            T a = m.getCompetitorA();
            T b = m.getCompetitorB();

            if (m.getWinner() == a) {
                records.get(a)[0]++;
                records.get(b)[1]++;
            } else {
                records.get(b)[0]++;
                records.get(a)[1]++;
            }
        }
        return records;
    }

    private List<T> getActivePlayers(List<T> participants, Map<T, int[]> records) {
        return participants.stream()
                .filter(p -> {
                    int[] r = records.get(p);
                    return r[0] < maxWins && r[1] < maxLosses;
                })
                .collect(Collectors.toList());
    }

    private Map<String, List<T>> groupByRecord(List<T> activePlayers, Map<T, int[]> records) {
        Map<String, List<T>> buckets = new HashMap<>();

        for (T p : activePlayers) {
            int[] r = records.get(p);
            String key = r[0] + "-" + r[1];
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }
        return buckets;
    }

    private void pairBuckets(Map<String, List<T>> buckets, List<Match<T>> output) {
        Random rand = new Random();

        for (List<T> group : buckets.values()) {
            Collections.shuffle(group, rand);

            for (int i = 0; i + 1 < group.size(); i += 2) {
                T a = group.get(i);
                T b = group.get(i + 1);

                output.add(createMatch.apply(a, b));
            }
        }
    }
}
