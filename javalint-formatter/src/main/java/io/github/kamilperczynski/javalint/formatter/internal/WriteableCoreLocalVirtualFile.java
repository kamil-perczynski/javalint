package io.github.kamilperczynski.javalint.formatter.internal;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.local.CoreLocalVirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Supplier;

public class WriteableCoreLocalVirtualFile extends VirtualFile {

  private final CoreLocalVirtualFile delegate;

  public WriteableCoreLocalVirtualFile(CoreLocalVirtualFile delegate) {
    this.delegate = delegate;
  }

  @Override
  public VirtualFileSystem getFileSystem() {
    return delegate.getFileSystem();
  }

  @Override

  public String getName() {
    return delegate.getName();
  }

  @Override

  public String getPath() {
    return delegate.getPath();
  }

  @Override
  public boolean isWritable() {
    return true;
  }

  @Override
  public boolean isDirectory() {
    return delegate.isDirectory();
  }

  @Override
  public boolean is(VFileProperty property) {
    return delegate.is(property);
  }

  @Override
  public long getTimeStamp() {
    return delegate.getTimeStamp();
  }

  @Override
  public long getLength() {
    return delegate.getLength();
  }

  @Override
  public boolean isValid() {
    return delegate.isValid();
  }

  @Override
  public VirtualFile getParent() {
    return delegate.getParent();
  }

  @Override
  public VirtualFile[] getChildren() {
    return delegate.getChildren();
  }

  @Override
  public OutputStream getOutputStream(Object requestor,
                                      long newModificationStamp,
                                      long newTimeStamp) throws IOException {
    return delegate.getOutputStream(requestor, newModificationStamp, newTimeStamp);
  }

  @Override
  public byte[] contentsToByteArray() throws IOException {
    return delegate.contentsToByteArray();
  }

  @Override
  public void refresh(boolean asynchronous, boolean recursive, Runnable postRunnable) {
    delegate.refresh(asynchronous, recursive, postRunnable);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return delegate.getInputStream();
  }

  @Override
  public long getModificationStamp() {
    return delegate.getModificationStamp();
  }

  @Override
  public boolean isInLocalFileSystem() {
    return delegate.isInLocalFileSystem();
  }

  @Override
  public boolean equals(Object o) {
    return delegate.equals(o);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override

  @NlsSafe
  public CharSequence getNameSequence() {
    return delegate.getNameSequence();
  }

  @Override

  public Path toNioPath() {
    return delegate.toNioPath();
  }

  @Override

  public String getUrl() {
    return delegate.getUrl();
  }

  @Override
  @Nullable
  @NlsSafe
  public String getExtension() {
    return delegate.getExtension();
  }

  @Override
  @NlsSafe

  public String getNameWithoutExtension() {
    return delegate.getNameWithoutExtension();
  }

  @Override
  public void rename(Object requestor, @NonNls String newName) throws IOException {
    delegate.rename(requestor, newName);
  }

  @Override
  public void setWritable(boolean writable) throws IOException {
    delegate.setWritable(writable);
  }

  @Override
  @Nullable
  public String getCanonicalPath() {
    return delegate.getCanonicalPath();
  }

  @Override
  @Nullable
  public VirtualFile getCanonicalFile() {
    return delegate.getCanonicalFile();
  }

  @Override
  @Nullable
  public VirtualFile findChild(@NonNls String name) {
    return delegate.findChild(name);
  }

  @Override

  public VirtualFile findOrCreateChildData(Object requestor,
                                           @NonNls String name) throws IOException {
    return delegate.findOrCreateChildData(requestor, name);
  }

  @Override
  public FileType getFileType() {
    return delegate.getFileType();
  }

  @Override
  @Nullable
  public VirtualFile findFileByRelativePath(@NonNls String relPath) {
    return delegate.findFileByRelativePath(relPath);
  }

  @Override

  public VirtualFile createChildDirectory(Object requestor,
                                          @NonNls String name) throws IOException {
    return delegate.createChildDirectory(requestor, name);
  }

  @Override

  public VirtualFile createChildData(Object requestor, @NonNls String name) throws IOException {
    return delegate.createChildData(requestor, name);
  }

  @Override
  public void delete(Object requestor) throws IOException {
    delegate.delete(requestor);
  }

  @Override
  public void move(Object requestor, VirtualFile newParent) throws IOException {
    delegate.move(requestor, newParent);
  }

  @Override

  public VirtualFile copy(Object requestor,
                          VirtualFile newParent,
                          @NonNls String copyName) throws IOException {
    return delegate.copy(requestor, newParent, copyName);
  }

  @Override
  public Charset getCharset() {
    return delegate.getCharset();
  }

  @Override
  public void setCharset(Charset charset) {
    delegate.setCharset(charset);
  }

  @Override
  public void setCharset(Charset charset, @Nullable Runnable whenChanged) {
    delegate.setCharset(charset, whenChanged);
  }

  @Override
  public void setCharset(Charset charset,
                         @Nullable Runnable whenChanged,
                         boolean fireEventsWhenChanged) {
    delegate.setCharset(charset, whenChanged, fireEventsWhenChanged);
  }

  @Override
  public boolean isCharsetSet() {
    return delegate.isCharsetSet();
  }

  @Override
  public void setBinaryContent(byte[] content,
                               long newModificationStamp,
                               long newTimeStamp) throws IOException {
    delegate.setBinaryContent(content, newModificationStamp, newTimeStamp);
  }

  @Override
  public void setBinaryContent(byte[] content,
                               long newModificationStamp,
                               long newTimeStamp,
                               Object requestor) throws IOException {
    delegate.setBinaryContent(content, newModificationStamp, newTimeStamp, requestor);
  }

  @Override
  public byte[] contentsToByteArray(boolean cacheContent) throws IOException {
    return delegate.contentsToByteArray(cacheContent);
  }

  @Override
  public void refresh(boolean asynchronous, boolean recursive) {
    delegate.refresh(asynchronous, recursive);
  }

  @Override

  @NlsSafe
  public String getPresentableName() {
    return delegate.getPresentableName();
  }

  @Override
  public long getModificationCount() {
    return delegate.getModificationCount();
  }

  @Override
  public byte[] getBOM() {
    return delegate.getBOM();
  }

  @Override
  public void setBOM(byte[] BOM) {
    delegate.setBOM(BOM);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean exists() {
    return delegate.exists();
  }

  @Override
  @Nullable
  @NlsSafe
  public String getDetectedLineSeparator() {
    return delegate.getDetectedLineSeparator();
  }

  @Override
  public void setDetectedLineSeparator(@Nullable String separator) {
    delegate.setDetectedLineSeparator(separator);
  }

  @Override
  public <T> T computeWithPreloadedContentHint(byte[] preloadedContentHint,
                                               Supplier<? extends T> computable) {
    return delegate.computeWithPreloadedContentHint(preloadedContentHint, computable);
  }

  @Override
  public boolean isRecursiveOrCircularSymlink() {
    return delegate.isRecursiveOrCircularSymlink();
  }

  @Override
  public boolean isCaseSensitive() {
    return delegate.isCaseSensitive();
  }


  @Override
  @TestOnly
  public String getUserDataString() {
    return delegate.getUserDataString();
  }

  @Override
  public void copyUserDataTo(UserDataHolderBase other) {
    delegate.copyUserDataTo(other);
  }

  @Override
  public <T> T getUserData(Key<T> key) {
    return delegate.getUserData(key);
  }


  @Override
  public <T> void putUserData(Key<T> key, @Nullable T value) {
    delegate.putUserData(key, value);
  }


  @Override
  public <T> T getCopyableUserData(Key<T> key) {
    return delegate.getCopyableUserData(key);
  }

  @Override
  public <T> void putCopyableUserData(Key<T> key, T value) {
    delegate.putCopyableUserData(key, value);
  }

  @Override
  public <T> boolean replace(Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
    return delegate.replace(key, oldValue, newValue);
  }

  @Override
  public <T> T putUserDataIfAbsent(Key<T> key, T value) {
    return delegate.putUserDataIfAbsent(key, value);
  }

  @Override
  public void copyCopyableDataTo(UserDataHolderBase clone) {
    delegate.copyCopyableDataTo(clone);
  }

  @Override
  public boolean isUserDataEmpty() {
    return delegate.isUserDataEmpty();
  }

}
