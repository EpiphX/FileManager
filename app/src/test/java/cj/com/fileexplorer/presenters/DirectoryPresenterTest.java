package cj.com.fileexplorer.presenters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;

import cj.com.fileexplorer.views.DirectoryView;
import cj.com.filemanager.FileIcon;
import cj.com.filemanager.FileManager;
import cj.com.filemanager.models.FileModel;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryPresenterTest {

    private DirectoryPresenter mDirectoryPresenter;

    @Mock
    private DirectoryView mDirectoryView;

    @Mock
    private FileManager mFileManager;


    @Before
    public void setup() {
        mDirectoryPresenter = new DirectoryPresenter(mDirectoryView, mFileManager);
    }

    /**
     * Tests on files requested.
     */
    @Test
    public void testOnFilesRequested() {
        ArrayList<FileModel> fileModels = new ArrayList<>(4);
        fileModels.add(new FileModel(new File(""), new FileIcon("pdf")));
        fileModels.add(new FileModel(new File(""), new FileIcon("pdf")));
        fileModels.add(new FileModel(new File(""), new FileIcon("pdf")));
        fileModels.add(new FileModel(new File(""), new FileIcon("pdf")));

        // Arrange
        Mockito.when(mFileManager.getCurrentDirectoryPath()).thenReturn("Test");
        Mockito.when(mFileManager.getNumberOfDirectoriesInCurrentDirectory()).thenReturn(4);
        Mockito.when(mFileManager.getNumberOfFilesInCurrentDirectory()).thenReturn(0);

        // Act
        mDirectoryPresenter.onFilesRequest();

        // Assert
        Mockito.verify(mDirectoryView, Mockito.atLeastOnce()).setDirectoryTitle("Test");
        Mockito.verify(mDirectoryView, Mockito.atLeastOnce()).showNumberOfDirectories(4);
        Mockito.verify(mDirectoryView, Mockito.atLeastOnce()).showNumberOfFiles(0);
    }

    @Test
    public void onShortFileModelDirectoryPressTest() {
        FileModel fileModel = Mockito.mock(FileModel.class);

        // Arrange
        Mockito.when(fileModel.isDirectory()).thenReturn(true);
        Mockito.when(fileModel.getAbsolutePath()).thenReturn("Test");

        // Act
        mDirectoryPresenter.onShortFileModelPress(fileModel);

        //Assert
        Mockito.verify(mFileManager, Mockito.atLeastOnce()).navigateToDirectory("Test");
    }

    @Test
    public void onShortFileModelFilePressTest() {
        FileModel fileModel = Mockito.mock(FileModel.class);

        // Arrange
        Mockito.when(fileModel.isDirectory()).thenReturn(false);

        mDirectoryPresenter.onShortFileModelPress(fileModel);

        //Assert
        Mockito.verify(mDirectoryView, Mockito.atLeastOnce()).viewFile(fileModel);
    }

    @Test
    public void onLongFileModelPressTest() {
        FileModel fileModel = Mockito.mock(FileModel.class);

        mDirectoryPresenter.onLongFileModelPress(fileModel);

        Mockito.verify(mDirectoryView, Mockito.atLeastOnce()).showExtendedInformationOnFile(fileModel);
    }

    @Test
    public void onNavigateToExternalStorageWhenAlreadyThereTest() {
        Mockito.when(mFileManager.getCurrentDirectoryPath()).thenReturn("Test");
        Mockito.when(mFileManager.getExternalStorageDirectoryPath()).thenReturn("Test");

        mDirectoryPresenter.onNavigateToExternalStorage();

        Mockito.verify(mFileManager, Mockito.atMost(0)).navigateToDirectory("Test");
    }

    @Test
    public void onNavigateToExternalStorageWhenNotThereTest() {
        Mockito.when(mFileManager.getCurrentDirectoryPath()).thenReturn("Different Directory");
        Mockito.when(mFileManager.getExternalStorageDirectoryPath()).thenReturn("Test");

        mDirectoryPresenter.onNavigateToExternalStorage();

        Mockito.verify(mFileManager, Mockito.atLeastOnce()).navigateToDirectory("Test");
    }

    @Test
    public void onNavigateToInternalStorageWhenAlreadyThereTest() {
        Mockito.when(mFileManager.getCurrentDirectoryPath()).thenReturn("Test");
        Mockito.when(mFileManager.getInternalStorageDirectoryPath()).thenReturn("Test");

        mDirectoryPresenter.onNavigateToInternalStorage();

        Mockito.verify(mFileManager, Mockito.atMost(0)).navigateToDirectory("Test");
    }

    @Test
    public void onNavigateToInternalStorageWhenNotThereTest() {
        Mockito.when(mFileManager.getCurrentDirectoryPath()).thenReturn("Different Directory");
        Mockito.when(mFileManager.getInternalStorageDirectoryPath()).thenReturn("Test");

        mDirectoryPresenter.onNavigateToInternalStorage();

        Mockito.verify(mFileManager, Mockito.atLeastOnce()).navigateToDirectory("Test");
    }

    @After
    public void tearDown() {
        mDirectoryPresenter = null;
    }
}