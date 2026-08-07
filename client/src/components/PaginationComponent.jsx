import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";

import React from "react";

const PaginationComponent = ({ activePage, totalPages, handlePageChange }) => {
  return (
    <Pagination>
      <PaginationContent>
        {/* {Previous Button} */}
        <PaginationItem>
          <PaginationPrevious
            onClick={handlePageChange}
            aria-disabled={activePage === 1}
            className={activePage === 1 ? "pointer-events-none opacity-50" : ""}
          />
        </PaginationItem>

        {/* {Always Show First Page} */}
        {activePage > 2 && (
          <>
            <PaginationItem>
              <PaginationLink onClick={() => handlePageChange(1)}>
                1
              </PaginationLink>
            </PaginationItem>
            {activePage > 3 && (
              <PaginationItem>
                <PaginationEllipsis />
              </PaginationItem>
            )}
          </>
        )}

        {/* {preview, current, next page} */}
        {activePage > 1 && (
          <PaginationItem>
            <PaginationLink onClick={() => handlePageChange(activePage - 1)}>
              {activePage - 1}
            </PaginationLink>
          </PaginationItem>
        )}

        <PaginationItem>
          <PaginationLink isActive>{activePage}</PaginationLink>
        </PaginationItem>

        {activePage < totalPages && (
          <PaginationItem>
            <PaginationLink onClick={() => handlePageChange(activePage + 1)}>
              {activePage + 1}
            </PaginationLink>
          </PaginationItem>
        )}

        {/* {Always show last page} */}
        {activePage < totalPages - 1 && (
          <>
            {activePage < totalPages - 2 && (
              <PaginationItem>
                <PaginationEllipsis />
              </PaginationItem>
            )}
            <PaginationItem>
              <PaginationLink onClick={() => handlePageChange(totalPages)}>
                {totalPages}
              </PaginationLink>
            </PaginationItem>
          </>
        )}

        {/* {Next Button} */}
        <PaginationItem>
          <PaginationNext
            onClick={handlePageChange}
            aria-disabled={activePage === totalPages}
            className={
              activePage === totalPages ? "pointer-events-none opacity-50" : ""
            }
          />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  );
};

export default PaginationComponent;
