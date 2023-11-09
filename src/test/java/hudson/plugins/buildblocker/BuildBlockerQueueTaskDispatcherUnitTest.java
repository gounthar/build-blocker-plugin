package hudson.plugins.buildblocker;

import hudson.model.AbstractProject;
import hudson.model.Node;
import hudson.model.Project;
import hudson.model.Queue;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.field;

public class BuildBlockerQueueTaskDispatcherUnitTest {

    @Mock
    private BlockingJobsMonitor monitor;
    @Mock
    private Node node;

    private AbstractProject project;
    private Queue.BuildableItem item;

    private BuildBlockerQueueTaskDispatcher dispatcher;

    @Before
    public void setup() throws IllegalAccessException {
        dispatcher = new BuildBlockerQueueTaskDispatcher(new FieldReturningMonitorFactory(monitor));

        project = Mockito.mock(AbstractProject.class);
        item = Mockito.mock(Queue.BuildableItem.class);
        field(Queue.Item.class, "task").set(item, project);
    }

    @BeforeEach
    void setUpStaticMocks() {
    }

    @AfterEach
    void tearDownStaticMocks() {
    }

    @Test
    public void testCanRunWithBuildBlockerDisabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanRunWithBuildBlockerEnabledAndNullBlockingJobsDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndCheckBuildableEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verify(monitor, times(1)).checkForBuildableQueueEntries(eq(item));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setScanAllQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verify(monitor, times(1)).checkForQueueEntries(eq(item));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndCheckBuildableEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setScanAllQueueItemStates()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verify(monitor, times(1)).checkForQueueEntries(eq(item));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithNodeEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanRunWithNodeEnabledAndCheckBuildableEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanRunWithNodeEnabledAndCheckAllEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setScanAllQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanRunWithNodeEnabledAndCheckAllEnabledAndCheckBuildableEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setScanAllQueueItemStates()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndNodeEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockOnNodeLevel()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndNodeEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockOnNodeLevel()
                                .setScanAllQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verify(monitor, times(1)).checkForQueueEntries(eq(item));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndNodeEnabledAndCheckBuildableEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockOnNodeLevel()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verify(monitor, times(1)).checkForBuildableQueueEntries(eq(item));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanRunWithGlobalEnabledAndNodeEnabledAndCheckAllEnabledAndCheckBuildableEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockOnNodeLevel()
                                .setScanAllQueueItemStates()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canRun(item);

        verify(monitor, times(1)).checkAllNodesForRunningBuilds();
        verify(monitor, times(1)).checkForQueueEntries(eq(item));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanTakeWithBuildBlockerDisabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithBuildBlockerEnabledAndNullBlockingJobsDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndCheckAllEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setScanAllQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndCheckBuildableEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndCheckBuildableEnabledAndCheckAllEnabledDoesNothing() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setScanBuildableQueueItemStates()
                                .setScanAllQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithNodeEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verify(monitor, times(1)).checkNodeForRunningBuilds(eq(node));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanTakeWithNodeEnabledAndCheckBuildableEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verify(monitor, times(1)).checkNodeForRunningBuilds(eq(node));
        verify(monitor, times(1)).checkNodeForBuildableQueueEntries(eq(item), eq(node));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanTakeWithNodeEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setScanAllQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verify(monitor, times(1)).checkNodeForRunningBuilds(eq(node));
        verify(monitor, times(1)).checkNodeForQueueEntries(eq(item), eq(node));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanTakeWithNodeEnabledAndCheckBuildableEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnNodeLevel()
                                .setScanAllQueueItemStates()
                                .setScanBuildableQueueItemStates()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verify(monitor, times(1)).checkNodeForRunningBuilds(eq(node));
        verify(monitor, times(1)).checkNodeForQueueEntries(eq(item), eq(node));
        verifyNoMoreInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndNodeEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(
                        new BuildBlockerPropertyBuilder()
                                .setUseBuildBlocker()
                                .setBlockOnGlobalLevel()
                                .setBlockOnNodeLevel()
                                .setBlockingJobs("someJob")
                                .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndNodeEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(new BuildBlockerPropertyBuilder()
                        .setUseBuildBlocker()
                        .setBlockOnGlobalLevel()
                        .setBlockOnNodeLevel()
                        .setScanAllQueueItemStates()
                        .setBlockingJobs("someJob")
                        .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndNodeEnabledAndCheckBuildableEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(new BuildBlockerPropertyBuilder()
                        .setUseBuildBlocker()
                        .setBlockOnGlobalLevel()
                        .setBlockOnNodeLevel()
                        .setScanBuildableQueueItemStates()
                        .setBlockingJobs("someJob")
                        .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    @Test
    public void testCanTakeWithGlobalEnabledAndNodeEnabledAndCheckBuildableEnabledAndCheckAllEnabledCallsCorrectMethods() {
        when(project.getProperty(eq(BuildBlockerProperty.class)))
                .thenReturn(new BuildBlockerPropertyBuilder()
                        .setUseBuildBlocker()
                        .setBlockOnGlobalLevel()
                        .setBlockOnNodeLevel()
                        .setScanBuildableQueueItemStates()
                        .setScanAllQueueItemStates()
                        .setBlockingJobs("someJob")
                        .createBuildBlockerProperty());

        dispatcher.canTake(node, item);

        verifyZeroInteractions(monitor);
    }

    private class FieldReturningMonitorFactory implements MonitorFactory {

        private BlockingJobsMonitor monitor;

        @Override
        public BlockingJobsMonitor build(String blockingJobs) {
            return monitor;
        }

        public FieldReturningMonitorFactory(BlockingJobsMonitor monitor) {
            this.monitor = monitor;
        }
    }
}
