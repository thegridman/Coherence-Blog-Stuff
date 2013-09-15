package com.thegridman.rail.schedules;

import com.tangosol.util.Resources;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.movements.TrainMovementActivationMessage;
import com.thegridman.rail.movements.TrainMovementCancellationMessage;
import com.thegridman.rail.movements.TrainMovementChangeOfOriginMessage;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.movements.TrainMovementMovementMessage;
import com.thegridman.rail.movements.TrainMovementReinstatementMessage;
import com.thegridman.utils.cli.CommandLineArg;
import com.thegridman.utils.cli.CommandLineParser;
import org.apache.commons.cli.Option;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * @author Jonathan Knight
 */
public class ScheduleFeedTiplocExtractorHandler extends ScheduleLoader.DefaultHandler
{
    private final String fileName;
    private final Set<String> stanoxToExtract;
    private PrintWriter writer;


    public ScheduleFeedTiplocExtractorHandler(String fileName, Collection<String> stanoxCodes)
    {
        this.fileName = fileName;
        this.stanoxToExtract = new HashSet<>();
        this.stanoxToExtract.addAll(stanoxCodes);
    }

    @Override
    public void handleHeaderLine(String header) throws IOException
    {
        GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(fileName));
        writer = new PrintWriter(new OutputStreamWriter(stream));
        writer.println(header);
        writer.flush();
    }

    @Override
    public void handleTiploc(Tiploc tiploc, String json) throws IOException
    {
        String stanox = tiploc.getStanox();
        if (stanoxToExtract.isEmpty() || (stanox != null && stanoxToExtract.contains(stanox)))
        {
            writer.println(json);
            writer.flush();
        }
    }

    @Override
    public void handleTrailerLine(String trailer)
    {
        writer.println(trailer);
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) throws Exception
    {
        args = new String[] {
                "-f", "/Users/jonathanknight/Projects/Git/Coherence-Blog-Stuff/data/rail/CIF_ALL_FULL_DAILY-toc-full.json.gzip",
                "-d", "/Users/jonathanknight/Projects/Git/Coherence-Blog-Stuff/data/rail/tiploc-cutdown-data.gzip",
                "-m", "/Users/jonathanknight/Projects/Git/Coherence-Blog-Stuff/coherence-twelve-one-two/src/test/resources/rail/data/multi-train.json,"
                    + "/Users/jonathanknight/Projects/Git/Coherence-Blog-Stuff/coherence-twelve-one-two/src/test/resources/rail/data/train-721H69MP02.json"
        };

        MainArgs mainArgs = CommandLineParser.parse(new MainArgs(), args);
        URL url = Resources.findFileOrResource(mainArgs.fileName, null);
        RailGsonParser parser = new RailGsonParser();

        Collection<String> stanoxCodes = getTiplocsFromMovements(mainArgs, parser);

        List<ScheduleLoader.Handler> handlers = new ArrayList<>();
        handlers.add(new LoggingScheduleLoaderHandler(System.out));
        handlers.add(new ScheduleFeedTiplocExtractorHandler(mainArgs.destination, stanoxCodes));

        ScheduleLoader.Handler handler = new MultiplexingScheduleLoaderHandler(handlers);

        ScheduleLoader loader = new ScheduleLoader(url, parser);
        loader.load(handler);
    }

    private static Set<String> getTiplocsFromMovements(MainArgs mainArgs, RailGsonParser parser) throws IOException
    {
        Set<String> stanoxCodes = new HashSet<>();

        if (mainArgs.movements != null && mainArgs.movements.length > 0)
        {
            for (String movementFile : mainArgs.movements)
            {
                List<TrainMovementMessage> messages = parser.loadAllTrainMovementMessage(movementFile);
                for (TrainMovementMessage message : messages)
                {
                    switch (message.getHeader().getMsgType())
                    {
                        case Activation:
                            TrainMovementActivationMessage activationMessage = (TrainMovementActivationMessage) message;
                            stanoxCodes.add(activationMessage.getSchedOriginStanox());
                            stanoxCodes.add(activationMessage.getTpOriginStanox());
                            break;
                        case Cancellation:
                            TrainMovementCancellationMessage cancellationMessage = (TrainMovementCancellationMessage) message;
                            stanoxCodes.add(cancellationMessage.getLocStanox());
                            stanoxCodes.add(cancellationMessage.getOriginalLocStanox());
                            break;
                        case Movement:
                            TrainMovementMovementMessage movementMessage = (TrainMovementMovementMessage) message;
                            stanoxCodes.add(movementMessage.getLocStanox());
                            stanoxCodes.add(movementMessage.getOriginalLocStanox());
                            stanoxCodes.add(movementMessage.getNextReportStanox());
                            stanoxCodes.add(movementMessage.getReportingStanox());
                            break;
                        case Reinstatement:
                            TrainMovementReinstatementMessage reinstatementMessage = (TrainMovementReinstatementMessage) message;
                            stanoxCodes.add(reinstatementMessage.getLocStanox());
                            stanoxCodes.add(reinstatementMessage.getOriginalLocStanox());
                            break;
                        case ChangeOfOrigin:
                            TrainMovementChangeOfOriginMessage changeOfOriginMessage = (TrainMovementChangeOfOriginMessage) message;
                            stanoxCodes.add(changeOfOriginMessage.getOriginalLocStanox());
                            break;
                    }
                }
            }
        }
        stanoxCodes.remove("");
        return stanoxCodes;
    }

    public static class MainArgs
    {
        @CommandLineArg(opt = "f", description = "The schedule gzip file name", required = true)
        public String fileName;

        @CommandLineArg(opt = "d", description = "The destination file", required = true)
        public String destination;

        @CommandLineArg(opt = "m", description = "The movements file to extract tiploc data for", required = false, numberOfArgs = Option.UNLIMITED_VALUES)
        public String[] movements;
    }

}
