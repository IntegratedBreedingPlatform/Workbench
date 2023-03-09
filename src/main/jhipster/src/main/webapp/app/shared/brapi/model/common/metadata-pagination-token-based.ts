export interface MetadataPaginationTokenBased {
  currentPageToken: string;
  nextPageToken: number;
  pageSize?: number;
  prevPageToken?: string;
  totalCount: number;
  totalPages: number;
}
