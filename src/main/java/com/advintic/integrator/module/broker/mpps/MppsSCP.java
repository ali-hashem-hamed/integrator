package com.advintic.integrator.module.broker.mpps;

/**
 *
 * @author Mahmoud
 */
import com.os.api.dicom.DicomUtility;
import static com.os.api.dicom.DicomUtility.readDicomFile;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.IOD;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.ValidationResult;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.TransferCapability;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.BasicMPPSSCP;
import org.dcm4che3.net.service.DicomServiceException;
import org.dcm4che3.net.service.DicomServiceRegistry;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.util.SafeClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MppsSCP {

    static MPPSHandler handler;
    private static ResourceBundle rb
            = ResourceBundle.getBundle("org.dcm4che3.tool.mppsscp.messages");

    private static final Logger LOG = LoggerFactory.getLogger(MppsSCP.class);

    private final Device device = new Device("mppsscp");
    private final ApplicationEntity ae = new ApplicationEntity("*");
    private final Connection conn = new Connection();
    private File storageDir;
    private IOD mppsNCreateIOD;
    private IOD mppsNSetIOD;

    private final BasicMPPSSCP mppsSCP = new BasicMPPSSCP() {

        @Override
        protected Attributes create(Association as, Attributes rq,
                Attributes rqAttrs, Attributes rsp) throws DicomServiceException {
            return MppsSCP.this.create(as, rq, rqAttrs);
        }

        @Override
        protected Attributes set(Association as, Attributes rq, Attributes rqAttrs,
                Attributes rsp) throws DicomServiceException {
            return MppsSCP.this.set(as, rq, rqAttrs);
        }
    };

    public MppsSCP() throws IOException {
        device.addConnection(conn);
        device.addApplicationEntity(ae);
        ae.setAssociationAcceptor(true);
        ae.addConnection(conn);
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        serviceRegistry.addDicomService(mppsSCP);
        ae.setDimseRQHandler(serviceRegistry);
    }

    public void setStorageDirectory(File storageDir) {
        if (storageDir != null) {
            storageDir.mkdirs();
        }
        this.storageDir = storageDir;
    }

    public File getStorageDirectory() {
        return storageDir;
    }

    private void setMppsNCreateIOD(IOD mppsNCreateIOD) {
        this.mppsNCreateIOD = mppsNCreateIOD;
    }

    private void setMppsNSetIOD(IOD mppsNSetIOD) {
        this.mppsNSetIOD = mppsNSetIOD;
    }

    public static void main(String[] args, MPPSHandler handler) {
        MppsSCP.handler = handler;
        try {
            CommandLine cl = parseComandLine(args);
            MppsSCP main = new MppsSCP();
            CLIUtils.configureBindServer(main.conn, main.ae, cl);
            CLIUtils.configure(main.conn, cl);
            configureTransferCapability(main.ae, cl);
            configureStorageDirectory(main, cl);
            configureIODs(main, cl);
            ExecutorService executorService = Executors.newCachedThreadPool();
            ScheduledExecutorService scheduledExecutorService
                    = Executors.newSingleThreadScheduledExecutor();
            main.device.setScheduledExecutor(scheduledExecutorService);
            main.device.setExecutor(executorService);
            main.device.bindConnections();
        } catch (ParseException e) {
            System.err.println("mppsscp: " + e.getMessage());
            System.err.println(rb.getString("try"));
            System.exit(2);
        } catch (Exception e) {
            System.err.println("mppsscp: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static CommandLine parseComandLine(String[] args) throws ParseException {
        Options opts = new Options();
        CLIUtils.addBindServerOption(opts);
        CLIUtils.addAEOptions(opts);
        CLIUtils.addCommonOptions(opts);
        addStorageDirectoryOptions(opts);
        addTransferCapabilityOptions(opts);
        addIODOptions(opts);
        return CLIUtils.parseComandLine(args, opts, rb, MppsSCP.class);
    }

    @SuppressWarnings("static-access")
    private static void addStorageDirectoryOptions(Options opts) {
        opts.addOption(null, "ignore", false,
                rb.getString("ignore"));
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("path")
                .withDescription(rb.getString("directory"))
                .withLongOpt("directory")
                .create(null));
    }

    @SuppressWarnings("static-access")
    private static void addTransferCapabilityOptions(Options opts) {
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("file|url")
                .withDescription(rb.getString("sop-classes"))
                .withLongOpt("sop-classes")
                .create(null));
    }

    @SuppressWarnings("static-access")
    private static void addIODOptions(Options opts) {
        opts.addOption(null, "no-validate", false,
                rb.getString("no-validate"));
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("file|url")
                .withDescription(rb.getString("ncreate-iod"))
                .withLongOpt("ncreate-iod")
                .create(null));
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("file|url")
                .withDescription(rb.getString("nset-iod"))
                .withLongOpt("nset-iod")
                .create(null));
    }

    private static void configureStorageDirectory(MppsSCP main, CommandLine cl) {
        if (!cl.hasOption("ignore")) {
            main.setStorageDirectory(
                    new File(cl.getOptionValue("directory", ".")));
        }
    }

    private static void configureIODs(MppsSCP main, CommandLine cl)
            throws IOException {
        if (!cl.hasOption("no-validate")) {
            main.setMppsNCreateIOD(IOD.load(
                    cl.getOptionValue("mpps-ncreate-iod",
                            "resource:mpps-ncreate-iod.xml")));
            main.setMppsNSetIOD(IOD.load(
                    cl.getOptionValue("mpps-nset-iod",
                            "resource:mpps-nset-iod.xml")));
        }
    }

    private static void configureTransferCapability(ApplicationEntity ae,
            CommandLine cl) throws IOException {
        Properties p = CLIUtils.loadProperties(
                cl.getOptionValue("sop-classes",
                        "resource:sop-classes.properties"),
                null);
        for (String cuid : p.stringPropertyNames()) {
            String ts = p.getProperty(cuid);
            ae.addTransferCapability(
                    new TransferCapability(null,
                            CLIUtils.toUID(cuid),
                            TransferCapability.Role.SCP,
                            CLIUtils.toUIDs(ts)));
        }
    }

    private Attributes create(Association as, Attributes rq, Attributes rqAttrs)
            throws DicomServiceException {
        checkPerformedProcedureStepStatus(rq, rqAttrs);
        if (mppsNCreateIOD != null) {
            ValidationResult result = rqAttrs.validate(mppsNCreateIOD);
            if (!result.isValid()) {
                throw DicomServiceException.valueOf(result, rqAttrs);
            }
        }
        if (storageDir == null) {
            return null;
        }
        String cuid = rq.getString(Tag.AffectedSOPClassUID);
        String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
        File file = new File(storageDir, iuid);
        if (file.exists()) {
            throw new DicomServiceException(Status.DuplicateSOPinstance).
                    setUID(Tag.AffectedSOPInstanceUID, iuid);
        }
        DicomOutputStream out = null;
        LOG.info("{}: M-WRITE {}", as, file);
        try {
            out = new DicomOutputStream(file);
            out.writeDataset(
                    Attributes.createFileMetaInformation(iuid, cuid,
                            UID.ExplicitVRLittleEndian),
                    rqAttrs);
        } catch (IOException e) {
            LOG.warn(as + ": Failed to store MPPS:", e);
            throw new DicomServiceException(Status.ProcessingFailure, e);
        } finally {
            SafeClose.close(out);
        }
        return null;
    }

    private Attributes set(Association as, Attributes rq, Attributes rqAttrs)
            throws DicomServiceException {
        checkPerformedProcedureStepStatus(rq, rqAttrs);
        if (mppsNSetIOD != null) {
            ValidationResult result = rqAttrs.validate(mppsNSetIOD);
            if (!result.isValid()) {
                throw DicomServiceException.valueOf(result, rqAttrs);
            }
        }
        if (storageDir == null) {
            return null;
        }
        String cuid = rq.getString(Tag.RequestedSOPClassUID);
        String iuid = rq.getString(Tag.RequestedSOPInstanceUID);
        File file = new File(storageDir, iuid);
        if (!file.exists()) {
            throw new DicomServiceException(Status.NoSuchObjectInstance).
                    setUID(Tag.AffectedSOPInstanceUID, iuid);
        }
        LOG.info("{}: M-UPDATE {}", as, file);
        Attributes data;
        DicomInputStream in = null;
        try {
            in = new DicomInputStream(file);
            data = in.readDataset(-1, -1);
        } catch (IOException e) {
            LOG.warn(as + ": Failed to read MPPS:", e);
            throw new DicomServiceException(Status.ProcessingFailure, e);
        } finally {
            SafeClose.close(in);
        }
        if (!"IN PROGRESS".equals(data.getString(Tag.PerformedProcedureStepStatus))) {
            BasicMPPSSCP.mayNoLongerBeUpdated();
        }

        data.addAll(rqAttrs);
        DicomOutputStream out = null;
        try {
            out = new DicomOutputStream(file);
            out.writeDataset(
                    Attributes.createFileMetaInformation(iuid, cuid, UID.ExplicitVRLittleEndian),
                    data);
        } catch (IOException e) {
            LOG.warn(as + ": Failed to update MPPS:", e);
            throw new DicomServiceException(Status.ProcessingFailure, e);
        } finally {
            SafeClose.close(out);
        }
        return null;
    }

    private void checkPerformedProcedureStepStatus(Attributes rq, Attributes rqAttrs) {
        String performedProcedureStepStatus = rqAttrs.getString(Tag.PerformedProcedureStepStatus);
        if ("IN PROGRESS".equals(performedProcedureStepStatus)) {

            System.out.println("@@@ IN PROGRESS @@@@");
            Sequence scheduledStepAttributesSequence = rqAttrs.getSequence(Tag.ScheduledStepAttributesSequence);
            String accessionNumber = scheduledStepAttributesSequence.get(0).getString(Tag.AccessionNumber);
            String patientID = rqAttrs.getString(Tag.PatientID);
            String affectedSOPInstanceUID = rq.getString(Tag.AffectedSOPInstanceUID);
            System.out.println("###################### AffectedSOPInstanceUID " + affectedSOPInstanceUID);
            System.out.println("###################### AccessionNumber " + accessionNumber);
            System.out.println("###################### PatientID " + patientID);
            handler.handleInProgress(accessionNumber);

        } else if ("COMPLETED".equals(performedProcedureStepStatus)) {

            System.out.println("@@@ COMPLETED @@@@");
            String requestedSOPInstanceUID = rq.getString(Tag.RequestedSOPInstanceUID);
            System.out.println("###################### RequestedSOPInstanceUID " + requestedSOPInstanceUID);
            File mppsFile = new File(storageDir, requestedSOPInstanceUID);
            String accessionNumber = getAccessionNumber(mppsFile);

            handler.handleCompleted(accessionNumber);
        }
    }

    private String getAccessionNumber(File mppsFile) {
        String accessionNumber = null;
        try {
            System.out.println("mppsFile " + mppsFile.getAbsolutePath());
            accessionNumber = DicomUtility.getDicomTagValue(mppsFile, "AccessionNumber");
            if (accessionNumber == null) {
                Attributes mppsDicomObject = readDicomFile(mppsFile);
                Attributes scheduledStepAttributesSequence = mppsDicomObject.getSequence(Tag.ScheduledStepAttributesSequence).get(0);
                accessionNumber = scheduledStepAttributesSequence.getString(Tag.AccessionNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessionNumber;
    }
}
