package com.onpositive.wikipedia.dumps.builder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Porter {

    private static final Pattern PERFECTIVEGROUND = Pattern.compile("((ив|ивши|ившись|ыв|ывши|ывшись)|((?<=[а€])(в|вши|вшись)))$");

    private static final Pattern REFLEXIVE = Pattern.compile("(с[€ь])$");

    private static final Pattern ADJECTIVE = Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|а€|€€|ою|ею)$");

    private static final Pattern PARTICIPLE = Pattern.compile("((ивш|ывш|ующ)|((?<=[а€])(ем|нн|вш|ющ|щ)))$");

    private static final Pattern VERB = Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|€т|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|((?<=[а€])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");

    private static final Pattern NOUN = Pattern.compile("(а|ев|ов|ие|ье|е|и€ми|€ми|ами|еи|ии|и|ией|ей|ой|ий|й|и€м|€м|ием|ем|ам|ом|о|у|ах|и€х|€х|ы|ь|ию|ью|ю|и€|ь€|€)$");

    private static final Pattern RVRE = Pattern.compile("^(.*?[аеиоуыэю€])(.*)$");

    private static final Pattern DERIVATIONAL = Pattern.compile(".*[^аеиоуыэю€]+[аеиоуыэю€].*ость?$");

    private static final Pattern DER = Pattern.compile("ость?$");

    private static final Pattern SUPERLATIVE = Pattern.compile("(ейше|ейш)$");

    private static final Pattern I = Pattern.compile("и$");
    private static final Pattern P = Pattern.compile("ь$");
    private static final Pattern NN = Pattern.compile("нн$");

    public static String stem(String word) {
        word = word.toLowerCase();
        word = word.replace('Є', 'е');
        Matcher m = RVRE.matcher(word);
        if (m.matches()) {
            String pre = m.group(1);
            String rv = m.group(2);
            String temp = PERFECTIVEGROUND.matcher(rv).replaceFirst("");
            if (temp.equals(rv)) {
                rv = REFLEXIVE.matcher(rv).replaceFirst("");
                temp = ADJECTIVE.matcher(rv).replaceFirst("");
                if (!temp.equals(rv)) {
                    rv = temp;
                    rv = PARTICIPLE.matcher(rv).replaceFirst("");
                } else {
                    temp = VERB.matcher(rv).replaceFirst("");
                    if (temp.equals(rv)) {
                        rv = NOUN.matcher(rv).replaceFirst("");
                    } else {
                        rv = temp;
                    }
                }

            } else {
                rv = temp;
            }

            rv = I.matcher(rv).replaceFirst("");

            if (DERIVATIONAL.matcher(rv).matches()) {
                rv = DER.matcher(rv).replaceFirst("");
            }

            temp = P.matcher(rv).replaceFirst("");
            if (temp.equals(rv)) {
                rv = SUPERLATIVE.matcher(rv).replaceFirst("");
                rv = NN.matcher(rv).replaceFirst("н");
            }else{
                rv = temp;
            }
            word = pre + rv;

        }

        return word;
    }

}

