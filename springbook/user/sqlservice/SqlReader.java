package springbook.user.sqlservice;

public interface SqlReader {
	// SQL�� �ܺο��� ������ SqlRegistry�� ����Ѵ�.
	// �پ��� ���ܰ� �߻��� �� �ְ����� ��κ� ���� �Ұ����� �����̹Ƿ� ���� ���ܸ� �����ص��� �ʴ´�.
	void read(SqlRegistry sqlRegistry);
}
